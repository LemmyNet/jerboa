package com.jerboa.ui.components.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jerboa.R
import com.jerboa.datatypes.getLocalizedListingTypeName
import com.jerboa.ui.components.common.ReadOnlyDropdown
import com.jerboa.ui.components.common.SortOptionsSelectorField
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.XL_PADDING
import it.vercruysse.lemmyapi.dto.ListingType
import it.vercruysse.lemmyapi.dto.SearchType
import it.vercruysse.lemmyapi.dto.SortType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchParametersField(
    currentSort: SortType,
    setCurrentSort: (SortType) -> Unit,
    currentSearchType: SearchType,
    setCurrentSearchType: (SearchType) -> Unit,
    searchTypeEnabled: Boolean,
    currentListing: ListingType,
    setCurrentListing: (ListingType) -> Unit,
) {
    val resources = LocalResources.current

    var expandedListing by rememberSaveable { mutableStateOf(false) }
    var expandedSearchType by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(XL_PADDING, MEDIUM_PADDING),
        verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
    ) {
        SortOptionsSelectorField(
            currentSort = currentSort,
            setCurrentSort = setCurrentSort,
        )

        ReadOnlyDropdown(
            expanded = expandedSearchType,
            setExpanded = { expandedSearchType = it },
            states = SearchType.entries,
            state = currentSearchType,
            setState = setCurrentSearchType,
            label = stringResource(R.string.search_in),
            stringTransform = { it.name },
            enabled = searchTypeEnabled,
        )
        ReadOnlyDropdown(
            expanded = expandedListing,
            setExpanded = { expandedListing = it },
            states = ListingType.entries,
            state = currentListing,
            setState = setCurrentListing,
            label = stringResource(R.string.limit_to),
            stringTransform = { getLocalizedListingTypeName(resources, it) },
        )
    }
}
