package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BlockInstance(
    val instance_id: InstanceId,
    val block: Boolean,
) : Parcelable
