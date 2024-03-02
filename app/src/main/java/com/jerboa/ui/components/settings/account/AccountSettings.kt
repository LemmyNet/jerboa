package com.jerboa.ui.components.settings.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.alorma.compose.settings.storage.disk.rememberBooleanSettingState
import com.alorma.compose.settings.storage.disk.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsCheckbox
import com.alorma.compose.settings.ui.SettingsList
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.datatypes.data
import com.jerboa.db.entity.Account
import com.jerboa.imageInputStreamFromUri
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.LargerCircularIcon
import com.jerboa.ui.components.common.MarkdownTextField
import com.jerboa.ui.components.common.PickImage
import com.jerboa.ui.components.common.PictrsBannerImage
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.muted
import it.vercruysse.lemmyapi.dto.ListingType
import it.vercruysse.lemmyapi.dto.SortType
import it.vercruysse.lemmyapi.dto.getSupportedEntries
import it.vercruysse.lemmyapi.v0x19.datatypes.SaveUserSettings
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
            modifier =
                Modifier
                    .padding(top = MEDIUM_PADDING)
                    .fillMaxWidth(),
            singleLine = true,
            keyboardOptions =
                KeyboardOptions.Default.copy(
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
            colors =
                IconButtonDefaults.iconButtonColors(
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
    siteViewModel: SiteViewModel,
    account: Account,
    padding: PaddingValues,
) {
    val luv =
        when (val siteRes = siteViewModel.siteRes) {
            is ApiState.Success -> siteRes.data.my_user?.local_user_view
            else -> null
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
    val supportedSortTypes = remember { getSupportedEntries<SortType>(API.version) }
    val defaultSortTypeInitial = luv?.local_user?.default_sort_type ?: SortType.Active
    val defaultSortType = rememberIntSettingState(
        key = "defaultSortType",
        defaultValue = supportedSortTypes.indexOf(defaultSortTypeInitial),
    )
    val defaultListingType =
        rememberIntSettingState(
            key = "defaultListingType",
            defaultValue = luv?.local_user?.default_listing_type?.ordinal ?: 0,
        )
    val showAvatars = rememberBooleanSettingState(
        key = "showAvatars",
        defaultValue = luv?.local_user?.show_avatars ?: false,
    )
    val showNsfw = rememberBooleanSettingState(
        key = "showNsfw",
        defaultValue = luv?.local_user?.show_nsfw ?: false,
    )
    val showScores = rememberBooleanSettingState(
        key = "showScores",
        defaultValue = luv?.local_user?.show_scores ?: false,
    )
    val showBotAccount = rememberBooleanSettingState(
        key = "showBotAccounts",
        defaultValue = luv?.local_user?.show_bot_accounts ?: false,
    )
    val botAccount = rememberBooleanSettingState(
        key = "botAccount",
        defaultValue = luv?.person?.bot_account ?: false,
    )
    val showReadPosts = rememberBooleanSettingState(
        key = "showReadPosts",
        defaultValue = luv?.local_user?.show_read_posts ?: false,
    )
    val sendNotificationsToEmail =
        rememberBooleanSettingState(
            key = "sendNotifsToEmail",
            defaultValue = luv?.local_user?.send_notifications_to_email ?: false,
        )
    val curr2FAEnabled = luv?.local_user?.totp_2fa_enabled ?: false
    val enable2FA = rememberBooleanSettingState(
        key = "enable2FA",
        defaultValue = curr2FAEnabled,
    )
    val sortTypeNames = remember { supportedSortTypes.map { ctx.getString(it.data.shortForm) } }

    siteViewModel.saveUserSettings =
        SaveUserSettings(
            display_name = displayName,
            bio = bio.text,
            email = email,
            avatar = avatar,
            banner = banner,
            matrix_user_id = matrixUserId,
            interface_language = interfaceLang,
            bot_account = botAccount.value,
            default_sort_type = supportedSortTypes[defaultSortType.value],
            send_notifications_to_email = sendNotificationsToEmail.value,
            show_avatars = showAvatars.value,
            show_bot_accounts = showBotAccount.value,
            show_nsfw = showNsfw.value,
            default_listing_type = ListingType.entries[defaultListingType.value],
            show_read_posts = showReadPosts.value,
            theme = theme,
            show_scores = showScores.value,
            discussion_languages = null,
        )
    var isUploadingAvatar by rememberSaveable { mutableStateOf(false) }
    var isUploadingBanner by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
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
                modifier =
                    Modifier
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
                            avatar = API.uploadPictrsImage(imageIs, ctx)
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
                            banner = API.uploadPictrsImage(imageIs, ctx)
                            isUploadingBanner = false
                        }
                    },
                )
            }
        }
        SettingsList(
            state = defaultListingType,
            title = { Text(text = stringResource(R.string.account_settings_default_listing_type)) },
            items =
                listOf(
                    stringResource(R.string.account_settings_all),
                    stringResource(R.string.account_settings_local),
                    stringResource(R.string.account_settings_subscribed),
                ),
        )
        SettingsList(
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
            enabled = email.isNotEmpty(),
            state = sendNotificationsToEmail,
            title = {
                Text(text = stringResource(R.string.account_settings_send_notifications_to_email))
            },
        )
        // TODO
//        SettingsCheckbox(
//            title = {
//                Text(text = stringResource(R.string.settings_enable_2fa))
//            },
//            state = enable2FA,
//        )
//
//        if (curr2FAEnabled) {
//            Row(
//                horizontalArrangement = Arrangement.Center,
//                modifier = Modifier.fillMaxWidth(),
//            ) {
//                OutlinedButton(
//                    onClick = {
//                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(luv!!.local_user.totp_2fa_url))
//                        ctx.startActivitySafe(intent)
//                    },
//                ) {
//                    Text(stringResource(R.string.settings_2fa_link))
//                }
//            }
//        }
    }
}
