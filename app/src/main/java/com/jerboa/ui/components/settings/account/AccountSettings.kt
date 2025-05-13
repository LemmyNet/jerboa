package com.jerboa.ui.components.settings.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.datatypes.data
import com.jerboa.datatypes.getLocalizedListingTypeName
import com.jerboa.db.entity.Account
import com.jerboa.imageInputStreamFromUri
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.LargerCircularIcon
import com.jerboa.ui.components.common.MarkdownTextField
import com.jerboa.ui.components.common.PickImage
import com.jerboa.ui.components.common.PictrsBannerImage
import com.jerboa.ui.theme.MEDIUM_PADDING
import it.vercruysse.lemmyapi.datatypes.SaveUserSettings
import it.vercruysse.lemmyapi.dto.ListingType
import it.vercruysse.lemmyapi.dto.SortType
import it.vercruysse.lemmyapi.dto.getSupportedEntries
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.ProvidePreferenceTheme
import me.zhanghai.compose.preference.SwitchPreference

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
                    autoCorrectEnabled = false,
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
                    containerColor = MaterialTheme.colorScheme.outline,
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
            is ApiState.Holder -> siteRes.data.my_user?.local_user_view
            else -> null
        }

    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val api = API.getInstanceOrNull()
    var displayName by rememberSaveable { mutableStateOf(luv?.person?.display_name.orEmpty()) }
    var bio by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(luv?.person?.bio.orEmpty()))
    }
    var email by rememberSaveable { mutableStateOf(luv?.local_user?.email) }
    var matrixUserId by rememberSaveable { mutableStateOf(luv?.person?.matrix_user_id.orEmpty()) }
    val theme by rememberSaveable { mutableStateOf(luv?.local_user?.theme.orEmpty()) }
    val interfaceLang by rememberSaveable {
        mutableStateOf(luv?.local_user?.interface_language.orEmpty())
    }
    var avatar by rememberSaveable { mutableStateOf(luv?.person?.avatar.orEmpty()) }
    var banner by rememberSaveable { mutableStateOf(luv?.person?.banner.orEmpty()) }
    val supportedSortTypes = remember { getSupportedEntries<SortType>(API.version) }
    val defaultSortTypeInitial = luv?.local_user?.default_sort_type ?: SortType.Active
    val defaultSortTypeState = remember { mutableStateOf(defaultSortTypeInitial) }

    val supportedListingTypes = remember { getSupportedEntries<ListingType>(API.version) }
    val defaultListingTypeState = remember { mutableStateOf(ListingType.entries[luv?.local_user?.default_listing_type?.ordinal ?: 0]) }

    val showNsfwState = remember { mutableStateOf(luv?.local_user?.show_nsfw ?: false) }
    val showAvatarsState = remember { mutableStateOf(luv?.local_user?.show_avatars ?: false) }
    val showScoresStateLegacy = remember { mutableStateOf(luv?.local_user?.show_scores ?: true) }
    val showScoresState = remember { mutableStateOf(luv?.local_user_vote_display_mode?.score ?: false) }
    val showUpvotesState = remember { mutableStateOf(luv?.local_user_vote_display_mode?.upvotes ?: false) }
    val showDownvotesState = remember { mutableStateOf(luv?.local_user_vote_display_mode?.downvotes ?: false) }
    val showUpvotePercentageState = remember { mutableStateOf(luv?.local_user_vote_display_mode?.upvote_percentage ?: false) }
    val showBotAccountState = remember { mutableStateOf(luv?.local_user?.show_bot_accounts ?: false) }
    val botAccountState = remember { mutableStateOf(luv?.person?.bot_account ?: false) }
    val showReadPostsState = remember { mutableStateOf(luv?.local_user?.show_read_posts ?: false) }
    val sendNotificationsToEmailState = remember { mutableStateOf(luv?.local_user?.send_notifications_to_email ?: false) }

    siteViewModel.saveUserSettings =
        SaveUserSettings(
            display_name = displayName,
            bio = bio.text,
            email = email,
            avatar = avatar,
            banner = banner,
            matrix_user_id = matrixUserId,
            interface_language = interfaceLang,
            bot_account = botAccountState.value,
            default_sort_type = supportedSortTypes[defaultSortTypeState.value.ordinal],
            send_notifications_to_email = sendNotificationsToEmailState.value,
            show_avatars = showAvatarsState.value,
            show_bot_accounts = showBotAccountState.value,
            show_nsfw = showNsfwState.value,
            default_listing_type = supportedListingTypes[defaultListingTypeState.value.ordinal],
            show_read_posts = showReadPostsState.value,
            theme = theme,
            show_scores = if (api != null && !api.FF.hidePost()) showScoresStateLegacy.value else showScoresState.value,
            show_upvotes = showUpvotesState.value,
            show_downvotes = showDownvotesState.value,
            show_upvote_percentage = showUpvotePercentageState.value,
            discussion_languages = null,
        )
    var isUploadingAvatar by rememberSaveable { mutableStateOf(false) }
    var isUploadingBanner by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding(),
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
            )
        }

        SettingsTextField(
            label = stringResource(R.string.account_settings_email),
            text = email ?: "",
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
        ProvidePreferenceTheme {
            ListPreference(
                type = ListPreferenceType.DROPDOWN_MENU,
                state = defaultListingTypeState,
                values = supportedListingTypes,
                valueToText = {
                    AnnotatedString(getLocalizedListingTypeName(ctx, it))
                },
                title = { Text(text = stringResource(R.string.account_settings_default_listing_type)) },
                summary = {
                    Text(getLocalizedListingTypeName(ctx, defaultListingTypeState.value))
                },
            )

            ListPreference(
                type = ListPreferenceType.DROPDOWN_MENU,
                state = defaultSortTypeState,
                values = supportedSortTypes,
                valueToText = {
                    AnnotatedString(ctx.getString(it.data.longForm))
                },
                title = { Text(text = stringResource(R.string.account_settings_default_sort_type)) },
                summary = {
                    Text(ctx.getString(defaultSortTypeState.value.data.longForm))
                },
            )

            SwitchPreference(
                state = showNsfwState,
                title = {
                    Text(text = stringResource(R.string.account_settings_show_nsfw))
                },
            )
            SwitchPreference(
                state = showAvatarsState,
                title = {
                    Text(text = stringResource(R.string.account_settings_show_avatars))
                },
            )
            SwitchPreference(
                state = showReadPostsState,
                title = {
                    Text(text = stringResource(R.string.account_settings_show_read_posts))
                },
            )
            SwitchPreference(
                state = botAccountState,
                title = {
                    Text(text = stringResource(R.string.account_settings_bot_account))
                },
            )
            SwitchPreference(
                state = showBotAccountState,
                title = {
                    Text(text = stringResource(R.string.account_settings_show_bot_accounts))
                },
            )

            if (api != null && !api.FF.hidePost()) {
                SwitchPreference(
                    state = showScoresStateLegacy,
                    title = {
                        Text(text = stringResource(R.string.account_settings_show_scores))
                    },
                )
            }

            if (api != null && api.FF.hidePost()) {
                SwitchPreference(
                    state = showScoresState,
                    title = {
                        Text(text = stringResource(R.string.account_settings_show_scores))
                    },
                )
                SwitchPreference(
                    state = showUpvotesState,
                    title = {
                        Text(text = stringResource(R.string.show_upvotes))
                    },
                )
                SwitchPreference(
                    state = showDownvotesState,
                    title = {
                        Text(text = stringResource(R.string.show_downvotes))
                    },
                )
                SwitchPreference(
                    state = showUpvotePercentageState,
                    title = {
                        Text(text = stringResource(R.string.show_upvote_percentage))
                    },
                )
            }

            SwitchPreference(
                enabled = !email.isNullOrEmpty(),
                state = sendNotificationsToEmailState,
                title = {
                    Text(text = stringResource(R.string.account_settings_send_notifications_to_email))
                },
            )
        }
    }
}
