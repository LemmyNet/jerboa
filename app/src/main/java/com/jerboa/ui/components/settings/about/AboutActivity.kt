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
) {
    Log.d("jerboa", "Got to About activity")

    val ctx = LocalContext.current

    @Suppress("DEPRECATION")
    val version = ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = "About", navController = navController)
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                SettingsMenuLink(
                    title = { Text("What's New") },
                    subtitle = { Text("Version $version") },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.NewReleases,
                            contentDescription = "TODO",
                        )
                    },
                    onClick = {
                        openLink("$githubUrl/blob/main/RELEASES.md", ctx)
                    },
                )
                SettingsDivider()
                SettingsHeader(text = "Support")
                SettingsMenuLink(
                    title = { Text("Issue tracker") },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.BugReport,
                            contentDescription = "TODO",
                        )
                    },
                    onClick = {
                        openLink("$githubUrl/issues", ctx)
                    },
                )
                SettingsMenuLink(
                    title = { Text("Developer Matrix chatroom") },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Chat,
                            contentDescription = "TODO",
                        )
                    },
                    onClick = {
                        openLink(jerboaMatrixChat, ctx)
                    },
                )
                SettingsMenuLink(
                    title = { Text("Donate to Jerboa development") },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.AttachMoney,
                            contentDescription = "TODO",
                        )
                    },
                    onClick = {
                        openLink(donateLink, ctx)
                    },
                )
                SettingsDivider()
                SettingsHeader(text = "Social")
                SettingsMenuLink(
                    title = { Text("Join c/jerboa") },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_jerboa),
                            modifier = Modifier.size(32.dp),
                            contentDescription = "TODO",
                        )
                    },
                    onClick = {
                        openLink(jerboaLemmyLink, ctx)
                    },
                )
                SettingsMenuLink(
                    title = { Text("Follow on Mastodon") },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.TravelExplore,
                            contentDescription = "TODO",
                        )
                    },
                    onClick = {
                        openLink(mastodonLink, ctx)
                    },
                )
                SettingsDivider()
                SettingsHeader(text = "Open source")
                SettingsMenuLink(
                    modifier = Modifier.padding(top = 20.dp),
                    title = { Text("Source code") },
                    subtitle = {
                        Text(
                            "Jerboa is libre open-source software, licensed under " +
                                "the GNU Affero General Public License v3.0",
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Code,
                            contentDescription = "TODO",
                        )
                    },
                    onClick = {
                        openLink(githubUrl, ctx)
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
    AboutActivity(navController = rememberNavController())
}
