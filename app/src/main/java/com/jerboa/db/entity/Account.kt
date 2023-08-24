package com.jerboa.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jerboa.feat.AccountVerificationState

@Entity
data class Account(
    @PrimaryKey val id: Int,
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
)

val AnonAccount = Account(
    -1,
    true,
    "",
    "Anonymous",
    "",
    1,
    0,
    verificationState = 0,
)

fun Account.isAnon(): Boolean {
    return this.id == -1
}

fun Account.isReady(): Boolean {
    return this.verificationState == AccountVerificationState.CHECKS_COMPLETE.ordinal
}

fun Account.getJWT(): String? {
    return if (isAnon()) null else this.jwt
}
