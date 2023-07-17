package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AdminPurgePersonView(
    val admin_purge_person: AdminPurgePerson,
    val admin: Person? = null,
) : Parcelable
