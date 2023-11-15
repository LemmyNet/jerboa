package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalSite(
    val id: LocalSiteId,
    val site_id: SiteId,
    val site_setup: Boolean,
    val enable_downvotes: Boolean,
    val enable_nsfw: Boolean,
    val community_creation_admin_only: Boolean,
    val require_email_verification: Boolean,
    val application_question: String? = null,
    val private_instance: Boolean,
    val default_theme: String,
    val default_post_listing_type: ListingType /* "All" | "Local" | "Subscribed" | "ModeratorView" */,
    val legal_information: String? = null,
    val hide_modlog_mod_names: Boolean,
    val application_email_admins: Boolean,
    val slur_filter_regex: String? = null,
    val actor_name_max_length: Int,
    val federation_enabled: Boolean,
    val captcha_enabled: Boolean,
    val captcha_difficulty: String,
    val published: String,
    val updated: String? = null,
    val registration_mode: RegistrationMode /* "Closed" | "RequireApplication" | "Open" */,
    val reports_email_admins: Boolean,
    val federation_signed_fetch: Boolean,
) : Parcelable
