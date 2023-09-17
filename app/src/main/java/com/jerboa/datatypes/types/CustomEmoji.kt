package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomEmoji(
    val id: CustomEmojiId,
    val local_site_id: LocalSiteId,
    val shortcode: String,
    val image_url: String,
    val alt_text: String,
    val category: String,
    val published: String,
    val updated: String? = null,
) : Parcelable
