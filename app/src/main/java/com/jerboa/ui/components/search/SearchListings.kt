package com.jerboa.ui.components.search

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonView

fun LazyListScope.searchPersonListings(
    personViews: List<PersonView>,
    onPersonClick: (PersonView) -> Unit,
) {
    items(personViews) { personView ->
        SearchListPersonItem(personView = personView, onClickPerson = onPersonClick)
    }
}
