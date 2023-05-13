package com.jerboa.datatypes.types

data class Instance(
    var id: InstanceId,
    var domain: String,
    var published: String,
    var updated: String? = null,
    var software: String? = null,
    var version: String? = null,
)