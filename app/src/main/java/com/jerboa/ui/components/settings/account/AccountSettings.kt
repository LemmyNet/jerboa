@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.settings.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.jerboa.R
import com.jerboa.api.uploadPictrsImage
import com.jerboa.datatypes.api.SaveUserSettings
import com.jerboa.db.Account
import com.jerboa.imageInputStreamFromUri
import com.jerboa.ui.components.common.*
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.theme.*
import kotlinx.coroutines.launch

// TODO replace all these
@Composable
fun SettingsTextField(
    label: String,
    text: String,
    onValueChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(SMALL_PADDING),
    ) {
        Text(text = label)
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Text,
                autoCorrect = false,
            ),
        )
    }
}

@Composable
fun ImageWithClose(
    onClick: () -> Unit,
    composable: @Composable () -> Unit,
) {
    Box(contentAlignment = Alignment.TopEnd) {
        composable()
        IconButton(onClick = onClick) {
            Icon(imageVector = Icons.Outlined.Close, contentDescription = stringResource(R.string.account_settings_remove_current_avatar))
        }
    }
}

@Composable
fun SettingsForm(
    accountSettingsViewModel: AccountSettingsViewModel,
    siteViewModel: SiteViewModel,
    account: Account?,
    onClickSave: (form: SaveUserSettings) -> Unit,
    padding: PaddingValues,
) {
    val luv = siteViewModel.siteRes?.my_user?.local_user_view
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    var displayName by rememberSaveable { mutableStateOf(luv?.person?.display_name.orEmpty()) }
    var bio by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(luv?.person?.bio.orEmpty()))
    }
    var email by rememberSaveable { mutableStateOf(luv?.local_user?.email.orEmpty()) }
    var matrixUserId by rememberSaveable { mutableStateOf(luv?.person?.matrix_user_id.orEmpty()) }
    val theme by rememberSaveable { mutableStateOf(luv?.local_user?.theme.orEmpty()) }
    val interfaceLang by rememberSaveable {
        mutableStateOf(luv?.local_user?.interface_language.orEmpty())
    }
    var avatar by rememberSaveable { mutableStateOf(luv?.person?.avatar.orEmpty()) }
    var banner by rememberSaveable { mutableStateOf(luv?.person?.banner.orEmpty()) }
    var defaultSortType by rememberSaveable { mutableStateOf(luv?.local_user?.default_sort_type) }
    var defaultListingType by rememberSaveable {
        mutableStateOf(luv?.local_user?.default_listing_type)
    }
    var showAvatars by rememberSaveable { mutableStateOf(luv?.local_user?.show_avatars) }
    var showNsfw by rememberSaveable { mutableStateOf(luv?.local_user?.show_nsfw ?: false) }
    var showScores by rememberSaveable { mutableStateOf(luv?.local_user?.show_scores) }
    var showBotAccount by rememberSaveable { mutableStateOf(luv?.local_user?.show_bot_accounts) }
    var botAccount by rememberSaveable { mutableStateOf(luv?.person?.bot_account) }
    var showReadPosts by rememberSaveable { mutableStateOf(luv?.local_user?.show_read_posts) }
    var showNewPostNotifs by rememberSaveable {
        mutableStateOf(luv?.local_user?.show_new_post_notifs)
    }
    var sendNotificationsToEmail by rememberSaveable {
        mutableStateOf(luv?.local_user?.send_notifications_to_email)
    }
    val form = SaveUserSettings(
        display_name = displayName,
        bio = bio.text,
        email = email,
        auth = account?.jwt ?: "",
        avatar = avatar,
        banner = banner,
        matrix_user_id = matrixUserId,
        interface_language = interfaceLang,
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
        show_scores = showScores,
        discussion_languages = null,
    )
    Column(
        modifier = Modifier
            .padding(padding)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(SMALL_PADDING),
    ) {
        SettingsTextField(
            label = stringResource(R.string.account_settings_display_name),
            text = displayName,
            onValueChange = { displayName = it },
        )
        Column {
            Text(stringResource(R.string.account_settings_bio))
            MarkdownTextField(
                text = bio,
                onTextChange = { bio = it },
                account = account,
                outlined = true,
                focusImmediate = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SMALL_PADDING),
            )
        }

        SettingsTextField(
            label = stringResource(R.string.account_settings_email),
            text = email,
            onValueChange = { email = it },
        )
        SettingsTextField(
            label = stringResource(R.string.account_settings_matrix_user),
            text = matrixUserId,
            onValueChange = { matrixUserId = it },
        )
        Text(text = stringResource(R.string.account_settings_avatar))
        if (avatar.isNotEmpty()) {
            ImageWithClose(onClick = { avatar = "" }) {
                LargerCircularIcon(icon = avatar)
            }
        } else {
            PickImage(onPickedImage = { uri ->
                val imageIs = imageInputStreamFromUri(ctx, uri)
                scope.launch {
                    account?.also { acct ->
                        avatar = uploadPictrsImage(acct, imageIs, ctx).orEmpty()
                    }
                }
            }, showImage = false)
        }
        Text(text = stringResource(R.string.account_settings_banner))
        if (banner.isNotEmpty()) {
            ImageWithClose(onClick = { banner = "" }) {
                PictrsBannerImage(url = banner)
            }
        } else {
            PickImage(onPickedImage = { uri ->
                val imageIs = imageInputStreamFromUri(ctx, uri)
                scope.launch {
                    account?.also { acct ->
                        banner = uploadPictrsImage(acct, imageIs, ctx).orEmpty()
                    }
                }
            }, showImage = false)
        }
        MyDropDown(
            suggestions = listOf(
                stringResource(R.string.account_settings_all),
                stringResource(R.string.account_settings_local),
                stringResource(R.string.account_settings_subscribed),
            ),
            onValueChange = { defaultListingType = it },
            defaultListingType ?: 0,
            label = stringResource(R.string.account_settings_default_listing_type),
        )
        MyDropDown(
            suggestions = listOf(
                stringResource(R.string.account_settings_active),
                stringResource(R.string.account_settings_hot),
                stringResource(R.string.account_settings_new),
                stringResource(R.string.account_settings_topday),
                stringResource(R.string.account_settings_topweek),
                stringResource(R.string.account_settings_topmonth),
                stringResource(R.string.account_settings_topyear),
                stringResource(R.string.account_settings_topall),
                stringResource(R.string.account_settings_mostcomments),
                stringResource(R.string.account_settings_newcomments),
            ),
            onValueChange = { defaultSortType = it },
            initialValue = defaultSortType ?: 0,
            label = stringResource(R.string.account_settings_default_sort_type),
        )

        MyCheckBox(
            checked = showNsfw,
            label = stringResource(R.string.account_settings_show_nsfw),
            onCheckedChange = { showNsfw = it },
        )
        MyCheckBox(
            checked = showAvatars == true,
            label = stringResource(R.string.account_settings_show_avatars),
            onCheckedChange = { showAvatars = it },
        )
        MyCheckBox(
            checked = showReadPosts == true,
            label = stringResource(R.string.account_settings_show_read_posts),
            onCheckedChange = { showReadPosts = it },
        )
        MyCheckBox(
            checked = botAccount == true,
            label = stringResource(R.string.account_settings_bot_account),
            onCheckedChange = { botAccount = it },
        )
        MyCheckBox(
            checked = showBotAccount == true,
            label = stringResource(R.string.account_settings_show_bot_accounts),
            onCheckedChange = { showBotAccount = it },
        )
        MyCheckBox(
            checked = showScores == true,
            label = stringResource(R.string.account_settings_show_scores),
            onCheckedChange = { showScores = it },
        )
        MyCheckBox(
            checked = showNewPostNotifs == true,
            label = stringResource(R.string.account_settings_show_notifications_for_new_posts),
            onCheckedChange = { showNewPostNotifs = it },
        )
        MyCheckBox(
            enabled = email.isNotEmpty(),
            checked = sendNotificationsToEmail == true,
            label = stringResource(R.string.account_settings_send_notifications_to_email),
            onCheckedChange = { sendNotificationsToEmail = it },
        )
        // Todo: Remove this
        Button(
            enabled = !accountSettingsViewModel.loading,
            onClick = { onClickSave(form) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.account_settings_save_settings))
        }
    }
}

@Preview
@Composable
fun MyCheckBoxPreview() {
    MyCheckBox(checked = true, label = " Test", onCheckedChange = {})
}
