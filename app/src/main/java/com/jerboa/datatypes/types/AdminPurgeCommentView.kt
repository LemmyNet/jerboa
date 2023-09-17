package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AdminPurgeCommentView(
    val admin_purge_comment: AdminPurgeComment,
    val admin: Person? = null,
    val post: Post,
) : Parcelable
