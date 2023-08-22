package com.jerboa.feat

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.core.content.getSystemService
import com.jerboa.JerboaAppState
import com.jerboa.MainActivity
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.datatypes.types.GetPersonDetails
import com.jerboa.datatypes.types.GetPersonDetailsResponse
import com.jerboa.datatypes.types.GetPersonMentions
import com.jerboa.datatypes.types.GetSite
import com.jerboa.datatypes.types.GetSiteResponse
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.isAnon
import com.jerboa.db.entity.isReady
import com.jerboa.isCurrentlyConnected
import com.jerboa.loginFirstToast
import com.jerboa.model.AccountViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.serializeToMap
import com.jerboa.toEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Request

// Order is important, as it classifies in which order it does the checks
enum class AccountVerificationState {

    /**
     * Base state, it has not done any checks yet.
     */
    NOT_CHECKED,

    /**
     * Checks if the user has network access
     */
    HAS_INTERNET,

    /**
     * Checks if the instance is alive
     *
     */
    INSTANCE_ALIVE,

    /**
     * Checks if the currentAccount is still alive
     */
    ACCOUNT_DELETED,

    /**
     * Checks if the account is banned
     */

    ACCOUNT_BANNED,

    /**
     * Checks if the JWT is not expired/removed
     */
    JWT_VERIFIED,

    /**
     * Checks if the site info could be retrieved
     */
    SITE_RETRIEVAL_SUCCEEDED,

    /**
     * All checks completed
     */
    CHECKS_COMPLETE,

    ;

    companion object {
        val size = entries.size
    }
}

fun checkInternet(ctx: Context): CheckState {
    return CheckState.from(ctx.getSystemService<ConnectivityManager>().isCurrentlyConnected())
}

// Checks the instance itself, this way we can properly check if the backend is having issues
// as the API endpoints seem te be returning 400 instead of 5XX when the backend is having internal issues
suspend fun checkInstance(instance: String): CheckState {
    return withContext(Dispatchers.IO) {
        try {
            val response = API.httpClient
                .newCall(Request("https://$instance".toHttpUrlOrNull()!!))
                .execute()

            if (response.isSuccessful) {
                CheckState.Passed
                // From experience some lemmy servers return this code when they are offline
            } else if (response.code == 521) {
                CheckState.ConnectionFailedMsg(instance)
            } else if (response.code >= 500) {
                CheckState.FailedMsg(instance)
            } else {
                CheckState.ConnectionFailedMsg(instance)
            }
        } catch (e: Exception) {
            Log.d("verification", "checkInstance error", e)
            CheckState.ConnectionFailedMsg(instance)
        }
    }
}

suspend fun checkIfAccountIsDeleted(
    account: Account,
    api: API,
): Pair<CheckState, ApiState.Success<GetPersonDetailsResponse>?> {
    return withContext(Dispatchers.IO) {
        val res = api.getPersonDetails(GetPersonDetails(person_id = account.id).serializeToMap())

        if (res.isSuccessful) {
            // This check is not perfect since, technically a different account with the same name and ID
            // can happen but that should be incredibly rare.
            return@withContext if (
                res.body()?.person_view?.person?.name.equals(account.name, true) &&
                res.body()?.person_view?.person?.deleted != true
            ) {
                Pair(CheckState.Passed, ApiState.Success<GetPersonDetailsResponse>(res.body()!!))
            } else {
                Pair(CheckState.Failed, null)
            }
        } else if (res.code() == 404) {
            return@withContext Pair(CheckState.Failed, null)
        } else {
            return@withContext Pair(CheckState.ConnectionFailed, null)
        }
    }
}

fun checkIfAccountIsBanned(
    userRes: GetPersonDetailsResponse,
): CheckState {
    return if (userRes.person_view.person.banned) {
        CheckState.FailedMsg(userRes.person_view.person.ban_expires ?: "TIME_NOT_SPECIFIED")
    } else {
        CheckState.Passed
    }
}

suspend fun checkIfJWTValid(account: Account, api: API): CheckState {
    return withContext(Dispatchers.IO) {
        // I could use any API endpoint that correctly checks the auth (there are some that don't ex: /site)
        val resp = api.getPersonMentions(GetPersonMentions(auth = account.jwt).serializeToMap())

        return@withContext if (resp.isSuccessful) {
            CheckState.Passed
            // Could check for exact body response `{"error":"not_logged_in"}` but could change over time and is unneeded
        } else if (resp.code() == 400) {
            CheckState.Failed
        } else {
            CheckState.ConnectionFailed
        }
    }
}

suspend fun checkIfSiteRetrievalSucceeded(
    siteViewModel: SiteViewModel,
    account: Account,
): Pair<CheckState, ApiState.Success<GetSiteResponse>?> {
    return when (val res = siteViewModel.siteRes) {
        is ApiState.Success -> {
            // Contains information for the wrong person
            if (res.data.my_user?.local_user_view?.local_user?.person_id == account.id) {
                Pair(CheckState.Passed, res)
            } else {
                siteViewModel.siteRes = ApiState.Empty
                checkIfSiteRetrievalSucceeded(siteViewModel, account)
            }
        }
        else -> {
            siteViewModel.getSite(GetSite(auth = account.jwt)).join()
            when (val res2 = siteViewModel.siteRes) {
                is ApiState.Success -> Pair(CheckState.Passed, res2)
                else -> Pair(CheckState.Failed, null)
            }
        }
    }
}

sealed class CheckState {
    object Passed : CheckState()
    object Failed : FailedMsg()
    object ConnectionFailed : ConnectionFailedMsg()

    open class ConnectionFailedMsg(val msg: String = "") : CheckState()
    open class FailedMsg(val msg: String = "") : CheckState()
    companion object {
        fun from(boolean: Boolean): CheckState {
            return if (boolean) Passed else Failed
        }
    }
}

suspend fun Account.checkAccountVerification(
    ctx: Context,
    siteViewModel: SiteViewModel,
    accountViewModel: AccountViewModel,
): Pair<AccountVerificationState, CheckState> {
    Log.d("verification", "Verification started")

    // Exceptions create by this API don't need to be shown, they are already handled
    val api = API.createTempInstance(this.instance) {
        Log.d("verification", "API ERROR", it)
        null
    }
    var checkState: CheckState = CheckState.Passed
    var curVerificationState: Int = if (this.verificationState >= AccountVerificationState.size) {
        AccountVerificationState.NOT_CHECKED.ordinal
    } else this.verificationState
    var userRes: ApiState.Success<GetPersonDetailsResponse>? = null

    // No check for the final state
    while (curVerificationState < AccountVerificationState.size - 1) {
        val verifyState = curVerificationState.toEnum<AccountVerificationState>()

        checkState = when (verifyState) {
            AccountVerificationState.NOT_CHECKED -> {
                // Anon account does not do any checks
                CheckState.from(this.id != -1)
            }

            AccountVerificationState.HAS_INTERNET -> {
                checkInternet(ctx)
            }

            AccountVerificationState.INSTANCE_ALIVE -> {
                checkInstance(this.instance)
            }

            AccountVerificationState.ACCOUNT_DELETED -> {
                val p = checkIfAccountIsDeleted(this, api)
                userRes = p.second
                p.first
            }

            AccountVerificationState.ACCOUNT_BANNED -> {
                checkIfAccountIsBanned(userRes!!.data)
            }

            AccountVerificationState.JWT_VERIFIED -> {
                checkIfJWTValid(this, api)
            }

            AccountVerificationState.SITE_RETRIEVAL_SUCCEEDED -> {
                checkIfSiteRetrievalSucceeded(siteViewModel, this).first
            }

            AccountVerificationState.CHECKS_COMPLETE -> {
                CheckState.Passed
            }
        }

        Log.d("verification", "Verified ${verifyState.name} with ${checkState::class.simpleName}")

        if (checkState != CheckState.Passed) {
            break
        }
        curVerificationState += 1
    }

    if (!this.isAnon()) {
        accountViewModel.setVerificationState(
            this@checkAccountVerification.id,
            if (curVerificationState == AccountVerificationState.CHECKS_COMPLETE.ordinal) {
                curVerificationState
            } else // Verification failed, thus we restart procedure
                AccountVerificationState.NOT_CHECKED.ordinal,
        )
    }

    return Pair(curVerificationState.toEnum<AccountVerificationState>(), checkState)
}

suspend fun Pair<AccountVerificationState, CheckState>.showSnackbarForVerificationInfo(
    ctx: Context,
    snackbarHostState: SnackbarHostState,
    loginAsToast: Boolean = false,
    actionPerform: suspend () -> Unit,
) {
    when (this.first) {
        AccountVerificationState.INSTANCE_ALIVE ->
            when (val t = this.second) {
                is CheckState.FailedMsg -> {
                    snackbarHostState.doSnackbarAction(
                        ctx.getString(R.string.verification_failed_instance, t.msg),
                        ctx.getString(R.string.retry),
                        actionPerform,
                    )
                }

                is CheckState.ConnectionFailedMsg -> {
                    snackbarHostState.doSnackbarAction(
                        ctx.getString(R.string.verification_connection_failed_instance, t.msg),
                        ctx.getString(R.string.retry),
                        actionPerform,
                    )
                }

                else -> {}
            }

        AccountVerificationState.ACCOUNT_BANNED -> {
            when (val t = this.second) {
                is CheckState.FailedMsg -> {
                    snackbarHostState.doSnackbarAction(
                        ctx.getString(R.string.verification_failed_user_banned, t.msg),
                        ctx.getString(R.string.retry),
                        actionPerform,
                    )
                }

                else -> {}
            }
        }

        else -> {}
    }

    when (this.first to this.second) {
        AccountVerificationState.NOT_CHECKED to CheckState.Failed ->
            if (loginAsToast) {
                loginFirstToast(ctx)
            } else {
                snackbarHostState.doSnackbarAction(
                    ctx.getString(R.string.verification_no_account),
                    ctx.getString(R.string.login_login),
                    actionPerform,
                )
            }

        AccountVerificationState.HAS_INTERNET to CheckState.Failed ->
            snackbarHostState.doSnackbarAction(
                ctx.getString(R.string.no_network),
                ctx.getString(R.string.retry),
                actionPerform,
            )

        AccountVerificationState.ACCOUNT_DELETED to CheckState.Failed ->
            snackbarHostState.doSnackbarAction(
                ctx.getString(R.string.verification_account_deleted),
                ctx.getString(R.string.verification_delete_account),
                actionPerform,
            )

        AccountVerificationState.ACCOUNT_DELETED to CheckState.ConnectionFailed -> {
            snackbarHostState.doSnackbarAction(
                ctx.getString(R.string.verification_failed_retrieve_profile),
                ctx.getString(R.string.retry),
                actionPerform,
            )
        }

        AccountVerificationState.JWT_VERIFIED to CheckState.Failed -> {
            snackbarHostState.doSnackbarAction(
                ctx.getString(R.string.verification_token_expired),
                ctx.getString(R.string.verification_login_again),
                actionPerform,
            )
        }

        AccountVerificationState.JWT_VERIFIED to CheckState.ConnectionFailed -> {
            snackbarHostState.doSnackbarAction(
                ctx.getString(R.string.verification_failed_verify_token),
                ctx.getString(R.string.retry),
                actionPerform,
            )
        }

        AccountVerificationState.SITE_RETRIEVAL_SUCCEEDED to CheckState.Failed -> {
            snackbarHostState.doSnackbarAction(
                ctx.getString(R.string.verification_failed_retrieve_site),
                ctx.getString(R.string.retry),
                actionPerform,
            )
        }
    }
}

val lockAccount = mutableSetOf<Account>()

suspend fun Account.isReadyAndIfNotDisplayInfo(
    appState: JerboaAppState,
    ctx: Context,
    snackbarHostState: SnackbarHostState,
    siteViewModel: SiteViewModel? = null,
    accountViewModel: AccountViewModel? = null,
    loginAsToast: Boolean = true,
): Boolean {
    val ready = isReady()
    if (!ready) {
        if (lockAccount.contains(this)) {
            Toast.makeText(ctx, ctx.getString(R.string.verification_account_busy, this.name), Toast.LENGTH_SHORT).show()
            return false
        } else {
            lockAccount.add(this)
            val siteVM = siteViewModel ?: (ctx as MainActivity).siteViewModel
            val accountVM = accountViewModel ?: (ctx as MainActivity).accountViewModel

            checkAccountVerification(ctx, siteVM, accountVM).let {
                lockAccount.remove(this)

                it.showSnackbarForVerificationInfo(
                    ctx,
                    snackbarHostState,
                    loginAsToast,
                ) {
                    when (it.first to it.second) {
                        AccountVerificationState.NOT_CHECKED to CheckState.Failed -> {
                            appState.toLogin()
                        }

                        AccountVerificationState.ACCOUNT_DELETED to CheckState.Failed -> {
                            accountVM.deleteAccountAndSwapCurrent(this, swapToAnon = true)
                            appState.toHome()
                        }

                        AccountVerificationState.JWT_VERIFIED to CheckState.Failed -> {
                            accountVM.deleteAccountAndSwapCurrent(this, swapToAnon = true)
                            appState.toLogin()
                        }

                        else -> this.isReadyAndIfNotDisplayInfo(
                            appState,
                            ctx,
                            snackbarHostState,
                            siteVM,
                            accountVM,
                            loginAsToast,
                        )
                    }
                }
                return it.first == AccountVerificationState.CHECKS_COMPLETE
            }
        }
    }

    return true
}

fun Account.doIfReadyElseDisplayInfo(
    appState: JerboaAppState,
    ctx: Context,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    siteViewModel: SiteViewModel? = null,
    accountViewModel: AccountViewModel? = null,
    loginAsToast: Boolean = true,
    doAction: (Account) -> Unit,
) {
    scope.launch {
        if (this@doIfReadyElseDisplayInfo.isReadyAndIfNotDisplayInfo(
                appState,
                ctx,
                snackbarHostState,
                siteViewModel,
                accountViewModel,
                loginAsToast,
            )
        ) {
            doAction(this@doIfReadyElseDisplayInfo)
        }
    }
}

suspend fun SnackbarHostState.doSnackbarAction(
    msg: String,
    btnText: String,
    actionPerform: suspend () -> Unit,
    duration: SnackbarDuration = SnackbarDuration.Long,
) {
    if (this.showSnackbar(msg, btnText, true, duration) == SnackbarResult.ActionPerformed) {
        actionPerform()
    }
}

fun Account.isReadyAndIfNotShowSimplifiedInfoToast(ctx: Context): Boolean {
    return if (this.isAnon()) {
        loginFirstToast(ctx)
        false
    } else if (!this.isReady()) {
        Toast.makeText(ctx, R.string.verification_account_not_read, Toast.LENGTH_SHORT).show()
        false
    } else {
        true
    }
}
