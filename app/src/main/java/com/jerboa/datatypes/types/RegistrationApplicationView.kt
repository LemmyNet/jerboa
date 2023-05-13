package com.jerboa.datatypes.types

data class RegistrationApplicationView(
    var registration_application: RegistrationApplication,
    var creator_local_user: LocalUser,
    var creator: Person,
    var admin: Person? = null,
)