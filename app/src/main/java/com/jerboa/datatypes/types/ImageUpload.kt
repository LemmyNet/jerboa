package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageUpload(
    val local_user_id: LocalUserId,
    val pictrs_alias: String,
    val pictrs_delete_token: String,
    val published: String,
) : Parcelable
