package com.jerboa.datatypes

import it.vercruysse.lemmyapi.v0x19.datatypes.Person


fun Person.getDisplayName(): String = this.display_name ?: this.name
