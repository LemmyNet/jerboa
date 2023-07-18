package com.jerboa.datatypes.types

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class GetSiteResponse(
    val site_view: SiteView,
    val admins: List<PersonView>,
    val version: String,
    val my_user: MyUserInfo? = null,
    val all_languages: List<Language>,
    val discussion_languages: List<LanguageId>,
    val taglines: List<Tagline>,
    val custom_emojis: List<CustomEmojiView>,
) : Parcelable
