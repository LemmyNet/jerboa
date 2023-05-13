package com.jerboa.datatypes.types

data class GetSiteResponse(
    var site_view: SiteView,
    var admins: Array<PersonView>,
    var online: Int,
    var version: String,
    var my_user: MyUserInfo? = null,
    var all_languages: Array<Language>,
    var discussion_languages: Array<LanguageId>,
    var taglines: Array<Tagline>,
    var custom_emojis: Array<CustomEmojiView>,
)