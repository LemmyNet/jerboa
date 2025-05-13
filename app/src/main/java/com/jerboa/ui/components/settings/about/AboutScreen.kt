package com.jerboa.ui.components.settings.about

import android.os.Build.VERSION.SDK_INT
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Anchor
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.profileinstaller.ProfileVerifier
import androidx.profileinstaller.ProfileVerifier.CompilationStatus
import com.jerboa.DONATE_LINK
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.ui.components.common.SimpleTopAppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceTheme

const val GITHUB_URL = "https://github.com/LemmyNet/jerboa"
const val JERBOA_MATRIX_CHAT = "https://matrix.to/#/#jerboa-dev:matrix.org"
const val JERBOA_LEMMY_ML_LINK = "https://lemmy.ml/c/jerboa"
const val MASTODON_LINK = "https://mastodon.social/@LemmyDev"
const val TORRENT_HELP_LINK = "https://join-lemmy.org/docs/users/02-media.html#torrents"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    onBack: () -> Unit,
    onClickCrashLogs: () -> Unit,
    openLinkRaw: (String, Boolean, Boolean) -> Unit,
) {
    Log.d("jerboa", "Got to About screen")

    val ctx = LocalContext.current
    val coroutine = rememberCoroutineScope()
    val version = ctx.packageManager.getPackageInfo(ctx.packageName, 0)?.versionName

    val snackbarHostState = remember { SnackbarHostState() }

    var profileState: ApiState<CompilationStatus> by remember {
        mutableStateOf(ApiState.Loading)
    }

    if (SDK_INT >= 28) {
        LaunchedEffect(Unit) {
            coroutine.launch(Dispatchers.IO) {
                val status = ProfileVerifier.getCompilationStatusAsync().get()
                profileState = ApiState.Success(status)
            }
        }
    } else {
        profileState = ApiState.Failure(Throwable("Not supported"))
    }

    fun openLink(link: String) {
        openLinkRaw(link, useCustomTabs, usePrivateTabs)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(
                text = stringResource(R.string.settings_about_about),
                onClickBack = onBack,
            )
        },
        content = { padding ->
            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(padding),
            ) {
                ProvidePreferenceTheme {
                    Preference(
                        title = { Text(stringResource(R.string.settings_about_what_s_new)) },
                        summary = {
                            Text(
                                stringResource(
                                    R.string.settings_about_version,
                                    version ?: "",
                                ),
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.NewReleases,
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            openLink("$GITHUB_URL/blob/main/RELEASES.md")
                        },
                    )
                    PreferenceCategory(
                        title = { Text(stringResource(R.string.settings_about_support)) },
                    )
                    Preference(
                        title = { Text(stringResource(R.string.settings_about_issue_tracker)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.BugReport,
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            openLink("$GITHUB_URL/issues")
                        },
                    )
                    Preference(
                        title = { Text(stringResource(R.string.crash_logs)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Build,
                                contentDescription = null,
                            )
                        },
                        onClick = onClickCrashLogs,
                    )
                    Preference(
                        title = { Text(stringResource(R.string.settings_about_developer_matrix_chatroom)) },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Chat,
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            openLink(JERBOA_MATRIX_CHAT)
                        },
                    )
                    Preference(
                        title = { Text(stringResource(R.string.settings_about_donate_to_jerboa_development)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.AttachMoney,
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            openLink(DONATE_LINK)
                        },
                    )
                    PreferenceCategory(
                        title = { Text(stringResource(R.string.about_social)) },
                    )
                    Preference(
                        title = { Text(stringResource(R.string.settings_about_join_c_jerboa)) },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_jerboa),
                                modifier = Modifier.size(24.dp),
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            openLink(JERBOA_LEMMY_ML_LINK)
                        },
                    )
                    Preference(
                        title = { Text(stringResource(R.string.settings_about_follow_on_mastodon)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.TravelExplore,
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            openLink(MASTODON_LINK)
                        },
                    )
                    PreferenceCategory(
                        title = {
                            Text(stringResource(R.string.settings_about_open_source))
                        },
                    )
                    Preference(
                        title = { Text(stringResource(R.string.settings_about_source_code)) },
                        summary = {
                            Text(
                                stringResource(R.string.settings_about_source_code_subtitle_part1) +
                                    stringResource(R.string.settings_about_source_code_subtitle_part2),
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Code,
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            openLink(GITHUB_URL)
                        },
                    )
                    PreferenceCategory(
                        title = {
                            Text(stringResource(R.string.info))
                        },
                    )

                    Preference(
                        title = { Text(stringResource(R.string.settings_about_baseline_profile)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Anchor,
                                contentDescription = null,
                            )
                        },
                        summary = {
                            Text(
                                text = when (val state = profileState) {
                                    is ApiState.Loading -> stringResource(R.string.loading)
                                    is ApiState.Success -> {
                                        val installed = stringResource(
                                            if (state.data.isCompiledWithProfile) {
                                                R.string.installed
                                            } else {
                                                R.string.not_installed
                                            },
                                        )

                                        val installCode = getInstallCode(state.data)

                                        "$installed\n$installCode"
                                    }

                                    is ApiState.Failure -> stringResource(R.string.unable_to_retrieve)
                                    else -> ""
                                },
                            )
                        },
                    )
                }
            }
        },
    )
}

@Preview
@Composable
fun AboutPreview() {
    AboutScreen(
        useCustomTabs = false,
        usePrivateTabs = false,
        onBack = {},
        onClickCrashLogs = {},
        openLinkRaw = { _: String, _: Boolean, _: Boolean -> },
    )
}

fun getInstallCode(compilationStatus: CompilationStatus): String =
    when (compilationStatus.profileInstallResultCode) {
        CompilationStatus.RESULT_CODE_NO_PROFILE_INSTALLED -> "NO_PROFILE_INSTALLED"
        CompilationStatus.RESULT_CODE_COMPILED_WITH_PROFILE -> "COMPILED_WITH_PROFILE"
        CompilationStatus.RESULT_CODE_PROFILE_ENQUEUED_FOR_COMPILATION -> "PROFILE_ENQUEUED_FOR_COMPILATION"
        CompilationStatus.RESULT_CODE_ERROR_UNSUPPORTED_API_VERSION -> "ERROR_UNSUPPORTED_API_VERSION"
        CompilationStatus.RESULT_CODE_ERROR_PACKAGE_NAME_DOES_NOT_EXIST -> "ERROR_PACKAGE_NAME_DOES_NOT_EXIST"
        CompilationStatus.RESULT_CODE_COMPILED_WITH_PROFILE_NON_MATCHING -> "COMPILED_WITH_PROFILE_NON_MATCHING"
        CompilationStatus.RESULT_CODE_ERROR_CACHE_FILE_EXISTS_BUT_CANNOT_BE_READ -> "ERROR_CACHE_FILE_EXISTS_BUT_CANNOT_BE_READ"
        CompilationStatus.RESULT_CODE_ERROR_CANT_WRITE_PROFILE_VERIFICATION_RESULT_CACHE_FILE ->
            "ERROR_CANT_WRITE_PROFILE_VERIFICATION_RESULT_CACHE_FILE"

        else -> "UNKNOWN[${compilationStatus.profileInstallResultCode}]"
    }
