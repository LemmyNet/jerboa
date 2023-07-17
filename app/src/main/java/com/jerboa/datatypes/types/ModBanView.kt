package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModBanView(
    val mod_ban: ModBan,
    val moderator: Person? = null,
    val banned_person: Person,
) : Parcelable
