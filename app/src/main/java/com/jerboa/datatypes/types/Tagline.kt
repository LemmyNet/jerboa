package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tagline(
    val id: Int,
    val local_site_id: LocalSiteId,
    val content: String,
    val published: String,
    val updated: String? = null,
) : Parcelable
