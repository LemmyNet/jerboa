package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeleteAccount(
    val password: String,
    val delete_content: Boolean,
) : Parcelable
