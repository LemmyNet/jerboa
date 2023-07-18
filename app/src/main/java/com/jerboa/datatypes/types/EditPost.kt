package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditPost(
    val post_id: PostId,
    val name: String? = null,
    val url: String? = null,
    val body: String? = null,
    val nsfw: Boolean? = null,
    val language_id: LanguageId? = null,
    val auth: String,
) : Parcelable
