@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.settings.about

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.jerboa.R
import com.jerboa.openLink
import com.jerboa.ui.components.common.SimpleTopAppBar

const val githubUrl = "https://github.com/dessalines/jerboa"
const val jerboaMatrixChat = "https://matrix.to/#/#jerboa-dev:matrix.org"
const val donateLink = "https://join-lemmy.org/donate"
const val jerboaLemmyLink = "https://lemmy.ml/c/jerboa"
const val mastodonLink = "https://mastodon.social/@LemmyDev"

@Composable
fun AboutActivity(
    navController: NavController,
    useCustomTabs: Boolean,
) {
    Log.d("jerboa", "Got to About activity")

    val ctx = LocalContext.current

    @Suppress("DEPRECATION")
    val version = ctx.packageManager.getPackageInfo(ctx.packageName, 0)?.versionName

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = stringResource(R.string.settings_about_about), navController = navController)
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
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
                        openLink("$githubUrl/blob/main/RELEASES.md", ctx, useCustomTabs)
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
                        openLink("$githubUrl/issues", ctx, useCustomTabs)
                    },
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
                        openLink(jerboaMatrixChat, ctx, useCustomTabs)
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
                        openLink(donateLink, ctx, useCustomTabs)
                    },
                )
                SettingsDivider()
                SettingsHeader(text = stringResource(R.string.about_social))
                SettingsMenuLink(
                    title = { Text(stringResource(R.string.settings_about_join_c_jerboa)) },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_jerboa),
                            modifier = Modifier.size(32.dp),
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        openLink(jerboaLemmyLink, ctx, useCustomTabs)
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
                        openLink(mastodonLink, ctx, useCustomTabs)
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
                        openLink(githubUrl, ctx, useCustomTabs)
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
    AboutActivity(navController = rememberNavController(), useCustomTabs = false)
}
