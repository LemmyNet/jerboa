package com.jerboa.ui.components.settings.account

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
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
import androidx.core.content.ContextCompat.startActivity
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsCheckbox
import com.alorma.compose.settings.ui.SettingsListDropdown
import com.jerboa.MAP_SORT_TYPE_SHORT_FORM
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.api.uploadPictrsImage
import com.jerboa.datatypes.types.ListingType
import com.jerboa.datatypes.types.SaveUserSettings
import com.jerboa.datatypes.types.SortType
import com.jerboa.db.entity.Account
import com.jerboa.imageInputStreamFromUri
import com.jerboa.model.AccountSettingsViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.*
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.muted
import kotlinx.coroutines.launch

@Composable
fun SettingsTextField(
    label: String,
    text: String,
    onValueChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(MEDIUM_PADDING),
    ) {
        Text(text = label)
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            modifier = Modifier
                .padding(top = MEDIUM_PADDING)
                .fillMaxWidth(),
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
        IconButton(
            onClick = onClick,
            // Hard to see close button without a contrasting background
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface.muted,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),

        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = stringResource(R.string.account_settings_remove_current_avatar),
            )
        }
    }
}

@Composable
fun SettingsForm(
    accountSettingsViewModel: AccountSettingsViewModel,
    siteViewModel: SiteViewModel,
    account: Account,
    onClickSave: (form: SaveUserSettings) -> Unit,
    padding: PaddingValues,
) {
    val luv = when (val siteRes = siteViewModel.siteRes) {
        is ApiState.Success -> siteRes.data.my_user?.local_user_view
        else -> {
            null
        }
    }

    val loading = when (accountSettingsViewModel.saveUserSettingsRes) {
        ApiState.Loading -> true
        else -> false
    }

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
    val defaultSortType = rememberIntSettingState(luv?.local_user?.default_sort_type?.ordinal ?: 0)
    val defaultListingType =
        rememberIntSettingState(luv?.local_user?.default_listing_type?.ordinal ?: 0)
    val showAvatars = rememberBooleanSettingState(luv?.local_user?.show_avatars ?: false)
    val showNsfw = rememberBooleanSettingState(luv?.local_user?.show_nsfw ?: false)
    val showScores = rememberBooleanSettingState(luv?.local_user?.show_scores ?: false)
    val showBotAccount = rememberBooleanSettingState(luv?.local_user?.show_bot_accounts ?: false)
    val botAccount = rememberBooleanSettingState(luv?.person?.bot_account ?: false)
    val showReadPosts = rememberBooleanSettingState(luv?.local_user?.show_read_posts ?: false)
    val showNewPostNotifs =
        rememberBooleanSettingState(luv?.local_user?.show_new_post_notifs ?: false)
    val sendNotificationsToEmail =
        rememberBooleanSettingState(luv?.local_user?.send_notifications_to_email ?: false)
    val curr2FAEnabled = luv?.local_user?.totp_2fa_url != null
    val enable2FA = rememberBooleanSettingState(curr2FAEnabled)

    val sortTypeNames = remember {
        MAP_SORT_TYPE_SHORT_FORM.values.map { ctx.getString(it) }
    }
    val form = SaveUserSettings(
        display_name = displayName,
        bio = bio.text,
        email = email,
        auth = account.jwt,
        avatar = avatar,
        banner = banner,
        matrix_user_id = matrixUserId,
        interface_language = interfaceLang,
        bot_account = botAccount.value,
        default_sort_type = SortType.entries[defaultSortType.value],
        send_notifications_to_email = sendNotificationsToEmail.value,
        show_avatars = showAvatars.value,
        show_bot_accounts = showBotAccount.value,
        show_nsfw = showNsfw.value,
        default_listing_type = ListingType.entries[defaultListingType.value],
        show_new_post_notifs = showNewPostNotifs.value,
        show_read_posts = showReadPosts.value,
        theme = theme,
        show_scores = showScores.value,
        discussion_languages = null,
        // True -> generates a new 2FA token, False -> removes current, null -> do nothing
        generate_totp_2fa = if (curr2FAEnabled == enable2FA.value) null else enable2FA.value,
    )
    var isUploadingAvatar by rememberSaveable { mutableStateOf(false) }
    var isUploadingBanner by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(padding)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
    ) {
        SettingsTextField(
            label = stringResource(R.string.account_settings_display_name),
            text = displayName,
            onValueChange = { displayName = it },
        )
        Column(
            modifier = Modifier.padding(MEDIUM_PADDING),
        ) {
            Text(stringResource(R.string.account_settings_bio))
            MarkdownTextField(
                text = bio,
                onTextChange = { bio = it },
                account = account,
                outlined = true,
                focusImmediate = false,
                modifier = Modifier
                    .fillMaxWidth(),
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
        Column(modifier = Modifier.padding(MEDIUM_PADDING)) {
            Text(text = stringResource(R.string.account_settings_avatar))
            if (avatar.isNotEmpty()) {
                ImageWithClose(onClick = { avatar = "" }) {
                    LargerCircularIcon(icon = avatar)
                }
            } else {
                PickImage(
                    isUploadingImage = isUploadingAvatar,
                    onPickedImage = { uri ->
                        val imageIs = imageInputStreamFromUri(ctx, uri)
                        scope.launch {
                            isUploadingAvatar = true
                            avatar = uploadPictrsImage(account, imageIs, ctx).orEmpty()
                            isUploadingAvatar = false
                        }
                    },
                )
            }
        }
        Column(modifier = Modifier.padding(MEDIUM_PADDING)) {
            Text(text = stringResource(R.string.account_settings_banner))
            if (banner.isNotEmpty()) {
                ImageWithClose(onClick = { banner = "" }) {
                    PictrsBannerImage(url = banner)
                }
            } else {
                PickImage(
                    isUploadingImage = isUploadingBanner,
                    onPickedImage = { uri ->
                        val imageIs = imageInputStreamFromUri(ctx, uri)
                        scope.launch {
                            isUploadingBanner = true
                            banner = uploadPictrsImage(account, imageIs, ctx).orEmpty()
                            isUploadingBanner = false
                        }
                    },
                )
            }
        }
        SettingsListDropdown(
            state = defaultListingType,
            title = { Text(text = stringResource(R.string.account_settings_default_listing_type)) },
            items = listOf(
                stringResource(R.string.account_settings_all),
                stringResource(R.string.account_settings_local),
                stringResource(R.string.account_settings_subscribed),
            ),
        )
        SettingsListDropdown(
            state = defaultSortType,
            title = { Text(text = stringResource(R.string.account_settings_default_sort_type)) },
            items = sortTypeNames,
        )

        SettingsCheckbox(
            state = showNsfw,
            title = {
                Text(text = stringResource(R.string.account_settings_show_nsfw))
            },
        )
        SettingsCheckbox(
            state = showAvatars,
            title = {
                Text(text = stringResource(R.string.account_settings_show_avatars))
            },
        )
        SettingsCheckbox(
            state = showReadPosts,
            title = {
                Text(text = stringResource(R.string.account_settings_show_read_posts))
            },
        )

        SettingsCheckbox(
            state = botAccount,
            title = {
                Text(text = stringResource(R.string.account_settings_bot_account))
            },
        )
        SettingsCheckbox(
            state = showBotAccount,
            title = {
                Text(text = stringResource(R.string.account_settings_show_bot_accounts))
            },
        )
        SettingsCheckbox(
            state = showScores,
            title = {
                Text(text = stringResource(R.string.account_settings_show_scores))
            },
        )
        SettingsCheckbox(
            state = showNewPostNotifs,
            title = {
                Text(text = stringResource(R.string.account_settings_show_notifications_for_new_posts))
            },
        )
        SettingsCheckbox(
            enabled = email.isNotEmpty(),
            state = sendNotificationsToEmail,
            title = {
                Text(text = stringResource(R.string.account_settings_send_notifications_to_email))
            },
        )

        SettingsCheckbox(
            title = {
                Text(text = stringResource(R.string.settings_enable_2fa))
            },
            state = enable2FA,
        )

        if (curr2FAEnabled) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(luv!!.local_user.totp_2fa_url))
                        ctx.startActivity(intent)
                    },

                ) {
                    Text(stringResource(R.string.settings_2fa_link))
                }
            }
        }

        // Todo: Remove this
        Button(
            enabled = !loading,
            onClick = { onClickSave(form) },
            modifier = Modifier
                .padding(MEDIUM_PADDING)
                .fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.account_settings_save_settings))
        }
    }
}
