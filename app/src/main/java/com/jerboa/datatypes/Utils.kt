package com.jerboa.datatypes

import com.jerboa.datatypes.types.Person

fun Person.getDisplayName(): String = this.display_name ?: this.name
