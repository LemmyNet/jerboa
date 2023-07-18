package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetFederatedInstancesResponse(
    val federated_instances: FederatedInstances? = null,
) : Parcelable
