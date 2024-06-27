package com.jerboa.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jerboa.datatypes.UserViewType
import com.jerboa.feat.AccountVerificationState

@Entity
data class Account(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "current") val current: Boolean,
    @ColumnInfo(name = "instance") val instance: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "jwt") val jwt: String,
    @ColumnInfo(
        name = "default_listing_type",
        defaultValue = "0",
    )
    val defaultListingType: Int,
    @ColumnInfo(
        name = "default_sort_type",
        defaultValue = "0",
    )
    val defaultSortType: Int,
    @ColumnInfo(
        name = "verification_state",
        defaultValue = "0",
    )
    val verificationState: Int,
    // These two are used to show extra bottom bar items right away
    @ColumnInfo(name = "is_admin") val isAdmin: Boolean,
    @ColumnInfo(name = "is_mod") val isMod: Boolean,
)

val AnonAccount =
    Account(
        id = -1,
        current = true,
        instance = "",
        name = "Anonymous",
        jwt = "",
        defaultListingType = 1,
        defaultSortType = 0,
        verificationState = 0,
        isAdmin = false,
        isMod = false,
    )

fun Account.isAnon(): Boolean = this.id == -1L

fun Account.isReady(): Boolean = this.verificationState == AccountVerificationState.CHECKS_COMPLETE.ordinal

fun Account.userViewType(): UserViewType =
    if (isAdmin) {
        UserViewType.AdminOnly
    } else if (isMod) {
        UserViewType.AdminOrMod
    } else {
        UserViewType.Normal
    }
