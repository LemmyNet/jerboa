
package com.jerboa.ui.components.settings.about

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.jerboa.R
import com.jerboa.ui.components.common.SimpleTopAppBar

const val GITHUB_URL = "https://github.com/dessalines/jerboa"
const val JERBOA_MATRIX_CHAT = "https://matrix.to/#/#jerboa-dev:matrix.org"
const val DONATE_LINK = "https://join-lemmy.org/donate"
const val JERBOA_LEMMY_ML_LINK = "https://lemmy.ml/c/jerboa"
const val MASTODON_LINK = "https://mastodon.social/@LemmyDev"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutActivity(
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    onBack: () -> Unit,
    onClickCrashLogs: () -> Unit,
    openLinkRaw: (String, Boolean, Boolean) -> Unit,
) {
    Log.d("jerboa", "Got to About activity")

    val ctx = LocalContext.current

    val version = ctx.packageManager.getPackageInfo(ctx.packageName, 0)?.versionName

    val snackbarHostState = remember { SnackbarHostState() }

    fun openLink(link: String) {
        openLinkRaw(link, useCustomTabs, usePrivateTabs)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = stringResource(R.string.settings_about_about), onClickBack = onBack)
        },
        content = { padding ->
            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(padding),
            ) {
                SettingsMenuLink(
                    title = { Text(stringResource(R.string.settings_about_what_s_new)) },
                    subtitle = { Text(stringResource(R.string.settings_about_version, version ?: "")) },
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
                SettingsDivider()
                SettingsHeader(text = stringResource(R.string.settings_about_support))
                SettingsMenuLink(
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
                SettingsMenuLink(
                    title = { Text(stringResource(R.string.crash_logs)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Build,
                            contentDescription = null,
                        )
                    },
                    onClick = onClickCrashLogs,
                )
                SettingsMenuLink(
                    title = { Text(stringResource(R.string.settings_about_developer_matrix_chatroom)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Chat,
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        openLink(JERBOA_MATRIX_CHAT)
                    },
                )
                SettingsMenuLink(
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
                SettingsDivider()
                SettingsHeader(text = stringResource(R.string.about_social))
                SettingsMenuLink(
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
                SettingsMenuLink(
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
                SettingsDivider()
                SettingsHeader(text = stringResource(R.string.settings_about_open_source))
                SettingsMenuLink(
                    modifier = Modifier.padding(top = 20.dp),
                    title = { Text(stringResource(R.string.settings_about_source_code)) },
                    subtitle = {
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
            }
        },
    )
}

@Composable
fun SettingsDivider() {
    Divider(modifier = Modifier.padding(vertical = 10.dp))
}

@Composable
fun SettingsHeader(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Text(
        text,
        modifier = Modifier.padding(start = 64.dp),
        color = color,
    )
}

@Preview
@Composable
fun AboutPreview() {
    AboutActivity(
        useCustomTabs = false,
        usePrivateTabs = false,
        onBack = {},
        onClickCrashLogs = {},
        openLinkRaw = { _: String, _: Boolean, _: Boolean -> },
    )
}
