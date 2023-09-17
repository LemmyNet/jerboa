package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetCommunity(
    val id: CommunityId? = null,
    val name: String? = null,
    val auth: String? = null,
) : Parcelable
