package com.jerboa.ui.components.ban

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.db.entity.isAnon
import com.jerboa.model.AccountViewModel
import com.jerboa.model.BanPersonViewModel
import com.jerboa.personNameShown
import com.jerboa.ui.components.common.ActionTopBar
import com.jerboa.ui.components.common.getCurrentAccount
import it.vercruysse.lemmyapi.datatypes.Person

object BanPersonReturn {
    const val PERSON_VIEW = "ban-person::return(person-view)"
    const val PERSON_SEND = "ban-person::send(person-view)"
}

@Composable
fun BanPersonScreen(
    appState: JerboaAppState,
    accountViewModel: AccountViewModel,
) {
    Log.d("jerboa", "got to ban person screen")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val banPersonViewModel: BanPersonViewModel = viewModel()
    val person = appState.getPrevReturn<Person>(key = BanPersonReturn.PERSON_SEND)

    var removeData by rememberSaveable { mutableStateOf(false) }
    var permaBan by rememberSaveable { mutableStateOf(false) }
    var expireDays: Long? by rememberSaveable { mutableStateOf(null) }

    var reason by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(""),
        )
    }

    val loading =
        when (banPersonViewModel.banPersonRes) {
            ApiState.Loading -> true
            else -> false
        }

    val focusManager = LocalFocusManager.current
    val title = stringResource(if (person.banned) R.string.unban_person else R.string.ban_person, personNameShown(person, true))

    val isBan = !person.banned

    // Make sure the form is valid only if permaban is checked or expireDays is not null
    val isValid = !isBan or permaBan or (expireDays !== null)

    Scaffold(
        topBar = {
            ActionTopBar(
                formValid = isValid && !loading,
                title = title,
                loading = loading,
                onActionClick = {
                    if (!account.isAnon()) {
                        banPersonViewModel.banOrUnbanPerson(
                            personId = person.id,
                            ban = isBan,
                            removeData = if (isBan) removeData else false,
                            expireDays = if (!isBan or permaBan) null else expireDays,
                            reason = reason.text,
                            ctx = ctx,
                            focusManager = focusManager,
                        ) { personView ->
                            appState.apply {
                                addReturn(BanPersonReturn.PERSON_VIEW, personView)
                                navigateUp()
                            }
                        }
                    }
                },
                actionText = R.string.form_submit,
                actionIcon = Icons.AutoMirrored.Outlined.Send,
                onBackClick = appState::popBackStack,
            )
        },
        content = { padding ->
            BanPersonBody(
                reason = reason,
                onReasonChange = { reason = it },
                isBan = isBan,
                expireDays = expireDays,
                onExpiresChange = {
                    expireDays = it
                    permaBan = false
                },
                permaBan = permaBan,
                onPermaBanChange = {
                    permaBan = it
                    expireDays = null
                },
                removeData = removeData,
                onRemoveDataChange = { removeData = it },
                isValid = isValid,
                account = account,
                padding = padding,
            )
        },
    )
}
