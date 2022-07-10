package com.jerboa.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.datatypes.api.SaveUserSettings
import com.jerboa.db.Account
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.theme.*


fun settingsClickWrapper(
    navController: NavController,
    account: Account?
) {
    account.also {
        navController.navigate(route = "settings")
    }
}

@Composable
fun SettingsHeader(
    navController: NavController = rememberNavController(),
) {

    val backgroundColor = MaterialTheme.colors.primarySurface
    val contentColor = contentColorFor(backgroundColor)

    TopAppBar(
        title = {
            Text(
                text = "Settings",
            )
        },
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = APP_BAR_ELEVATION,
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
    )
}

@Composable
fun SettingsField(
    label: String,
    placeholder: String? = null,
    text: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(SMALL_PADDING)
    ) {
        Text(text = label)
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            singleLine = true,
            placeholder = { placeholder?.let { Text(text = it) } },
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Text,
                autoCorrect = false,
            )
        )
    }

}

@Composable
fun SettingsCheckBox(
    checked: Boolean,
    enabled: Boolean = true,
    label: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.padding(SMALL_PADDING)
    ) {
        Text(text = label, modifier = Modifier.fillMaxHeight())
        Checkbox(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
    }

}

@Composable
fun SettingsForm(
    settingsViewModel: SettingsViewModel,
    siteViewModel: SiteViewModel,
    account: Account?,
    onClickSettings: (form: SaveUserSettings) -> Unit,
) {

    val luv = siteViewModel.siteRes?.my_user?.local_user_view


    var displayName by rememberSaveable { mutableStateOf(luv?.person?.display_name.orEmpty()) }
    var bio by rememberSaveable { mutableStateOf(luv?.person?.bio.orEmpty()) }
    var email by rememberSaveable { mutableStateOf(luv?.local_user?.email.orEmpty()) }
    var matrixUserId by rememberSaveable { mutableStateOf(luv?.person?.matrix_user_id.orEmpty()) }
    val theme by rememberSaveable { mutableStateOf(luv?.local_user?.theme.orEmpty()) }
    val lang by rememberSaveable { mutableStateOf(luv?.local_user?.lang.orEmpty()) }
    var avatar by rememberSaveable { mutableStateOf(luv?.person?.avatar.orEmpty()) }
    var banner by rememberSaveable { mutableStateOf(luv?.person?.banner.orEmpty()) }
    var defaultSortType by rememberSaveable { mutableStateOf(luv?.local_user?.default_sort_type) }
    var defaultListingType by rememberSaveable { mutableStateOf(luv?.local_user?.default_listing_type) }
    var showAvatars by rememberSaveable { mutableStateOf(luv?.local_user?.show_avatars) }
    var showNsfw by rememberSaveable { mutableStateOf(luv?.local_user?.show_nsfw ?: false) }
    var showScores by rememberSaveable { mutableStateOf(luv?.local_user?.show_scores) }
    var showBotAccount by rememberSaveable { mutableStateOf(luv?.local_user?.show_bot_accounts) }
    var botAccount by rememberSaveable { mutableStateOf(luv?.person?.bot_account) }
    var showReadPosts by rememberSaveable { mutableStateOf(luv?.local_user?.show_read_posts) }
    var showNewPostNotifs by rememberSaveable { mutableStateOf(luv?.local_user?.show_new_post_notifs) }
    var sendNotificationsToEmail by rememberSaveable { mutableStateOf(luv?.local_user?.send_notifications_to_email) }


    val form = SaveUserSettings(
        display_name = displayName,
        bio = bio,
        email = email,
        auth = account?.jwt,
        avatar = avatar,
        banner = banner,
        matrix_user_id = matrixUserId,
        lang = lang,
        bot_account = botAccount,
        default_sort_type = defaultSortType,
        send_notifications_to_email = sendNotificationsToEmail,
        show_avatars = showAvatars,
        show_bot_accounts = showBotAccount,
        show_nsfw = showNsfw,
        default_listing_type = defaultListingType,
        show_new_post_notifs = showNewPostNotifs,
        show_read_posts = showReadPosts,
        theme = theme,
        show_scores = showScores
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SMALL_PADDING)
            .verticalScroll(rememberScrollState())
    ) {
        SettingsField(
            label = "Display Name",
            text = displayName,
            onValueChange = { displayName = it }
        )
        SettingsField(
            label = "Bio",
            text = bio,
            onValueChange = { bio = it }
        )
        SettingsField(
            label = "Email",
            text = email,
            onValueChange = { email = it }
        )
        SettingsField(
            label = "Matrix User",
            text = matrixUserId,
            onValueChange = { matrixUserId = it }
        )
        Divider()
        SettingsCheckBox(
            checked = showNsfw,
            label = "Show NSFW",
            onCheckedChange = { showNsfw = it }
        )
        SettingsCheckBox(
            checked = showAvatars == true,
            label = "Show Avatars",
            onCheckedChange = { showAvatars = it }
        )
        SettingsCheckBox(
            checked = showReadPosts == true,
            label = "Show Read Posts",
            onCheckedChange = { showReadPosts = it }
        )
        SettingsCheckBox(
            checked = botAccount == true,
            label = "Bot Account",
            onCheckedChange = { botAccount = it }
        )
        SettingsCheckBox(
            checked = showBotAccount == true,
            label = "Show Bot Accounts",
            onCheckedChange = { showBotAccount = it }
        )
        SettingsCheckBox(
            checked = showScores == true,
            label = "Show Scores",
            onCheckedChange = { showScores = it }
        )
        SettingsCheckBox(
            checked = showNewPostNotifs == true,
            label = "Show Notifications for New Posts",
            onCheckedChange = { showNewPostNotifs = it }
        )
        SettingsCheckBox(
            enabled = email.isNotEmpty(),
            checked = sendNotificationsToEmail == true,
            label = "Send Notifications to Email",
            onCheckedChange = { sendNotificationsToEmail = it }
        )

        Button(
            enabled = !settingsViewModel.loading,
            onClick = { onClickSettings(form) }
        ) {
            Text(text = "Save Settings")
        }

    }

}

@Preview
@Composable
fun SettingsCheckBoxPreview() {
    SettingsCheckBox(checked = true, label = " Test", onCheckedChange = {})
}
