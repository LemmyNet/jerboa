package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InstanceWithFederationState(
    val id: InstanceId,
    val domain: String,
    val published: String,
    val updated: String? = null,
    val software: String? = null,
    val version: String? = null,
    val federation_state: ReadableFederationState? = null,
) : Parcelable
