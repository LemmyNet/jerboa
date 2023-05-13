package com.jerboa.datatypes.types

data class FederatedInstances(
    var linked: Array<Instance>,
    var allowed: Array<Instance>,
    var blocked: Array<Instance>,
)