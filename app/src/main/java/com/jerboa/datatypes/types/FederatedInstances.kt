package com.jerboa.datatypes.types

data class FederatedInstances(
    val linked: List<Instance>,
    val allowed: List<Instance>,
    val blocked: List<Instance>,
)
