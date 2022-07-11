package com.jerboa.ui.components.settings

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.api.uploadPictrsImage
import com.jerboa.datatypes.api.SaveUserSettings
import com.jerboa.db.Account
import com.jerboa.decodeUriToBitmap
import com.jerboa.imageInputStreamFromUri
import com.jerboa.ui.components.common.LargerCircularIcon
import com.jerboa.ui.components.common.PictrsBannerImage
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.theme.*
import kotlinx.coroutines.launch

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
fun PickAvatarOrBanner(
    onPickedAvatarOrBanner: (image: Uri) -> Unit,
    type: String
) {
    val ctx = LocalContext.current
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        bitmap.value = decodeUriToBitmap(ctx, imageUri!!)
        Log.d("jerboa", imageUri.toString())
        onPickedAvatarOrBanner(uri!!)
    }

    OutlinedButton(onClick = {
        launcher.launch("image/*")
    }) {
        Text(
            text = "Upload $type",
            color = MaterialTheme.colors.onBackground.muted,
        )
    }


}

//https://stackoverflow.com/a/67111599
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsDropDown(
    suggestions: List<String>,
    onValueChange: (Int) -> Unit,
    initialValue: Int,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(suggestions[initialValue]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            readOnly = true,
            value = selectedText,
            onValueChange = { },
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            suggestions.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        selectedText = selectionOption
                        expanded = false
                        onValueChange(suggestions.indexOf(selectedText))
                    }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }

}

@Composable
fun BannerWithClose(url: String, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.TopEnd) {
        PictrsBannerImage(url = url)
        IconButton(onClick = onClick) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove Current Banner")
        }

    }
}

@Composable
fun AvatarWithClose(url: String, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.TopEnd) {
        LargerCircularIcon(icon = url)
        IconButton(onClick = onClick) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove Current Avatar")
        }
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
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current


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
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(SMALL_PADDING)
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
        Text(text = "Avatar")
        if (avatar.isNotEmpty()) {
            AvatarWithClose(url = avatar, onClick = { avatar = "" })
        } else {
            PickAvatarOrBanner(onPickedAvatarOrBanner = { uri ->
                val imageIs = imageInputStreamFromUri(ctx, uri)
                scope.launch {
                    account?.also { acct ->
                        avatar = uploadPictrsImage(acct, imageIs, ctx).orEmpty()
                    }
                }

            }, type = "Avatar")
        }
        Text(text = "Banner")
        if (banner.isNotEmpty()) {
            BannerWithClose(url = banner, onClick = {
                banner = ""
            })
        } else {
            PickAvatarOrBanner(onPickedAvatarOrBanner = { uri ->
                val imageIs = imageInputStreamFromUri(ctx, uri)
                scope.launch {
                    account?.also { acct ->
                        banner = uploadPictrsImage(acct, imageIs, ctx).orEmpty()
                    }
                }

            }, type = "Banner")
        }



        Divider()
        SettingsDropDown(
            suggestions = listOf("All", "Local", "Subscribed"),
            onValueChange = { defaultListingType = it }, defaultListingType ?: 0,
            label = "Default Listing Type"
        )
        SettingsDropDown(
            suggestions = listOf(
                "Active",
                "Hot",
                "New",
                "TopDay",
                "TopWeek",
                "TopMonth",
                "TopYear",
                "TopAll",
                "MostComments",
                "NewComments"
            ),
            onValueChange = { defaultSortType = it }, defaultSortType ?: 0,
            label = "Default Sort Type"
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
