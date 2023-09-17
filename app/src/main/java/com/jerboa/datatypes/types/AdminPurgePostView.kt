package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AdminPurgePostView(
    val admin_purge_post: AdminPurgePost,
    val admin: Person? = null,
    val community: Community,
) : Parcelable
