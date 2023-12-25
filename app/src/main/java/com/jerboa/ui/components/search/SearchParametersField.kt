package com.jerboa.ui.components.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jerboa.R
import com.jerboa.datatypes.data
import com.jerboa.datatypes.getLocalizedListingTypeName
import com.jerboa.ui.components.common.ClickableOutlinedTextField
import com.jerboa.ui.components.common.ReadOnlyDropdown
import com.jerboa.ui.components.common.SortOptionsDropdown
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
    currentListing: ListingType,
    setCurrentListing: (ListingType) -> Unit,
) {
    val ctx = LocalContext.current

    var expandedListing by rememberSaveable { mutableStateOf(false) }
    var expandedSearchType by rememberSaveable { mutableStateOf(false) }
    var expandedSort by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box {
            ClickableOutlinedTextField(
                value = stringResource(currentSort.data.shortForm),
                label = { Text(stringResource(R.string.selectSort)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSort) },
                onClick = { expandedSort = true },
            )

            SortOptionsDropdown(
                expanded = expandedSort,
                onDismissRequest = { expandedSort = false },
                onClickSortType = {
                    expandedSort = false
                    setCurrentSort(it)
                },
                selectedSortType = currentSort,
                fixedWidth = OutlinedTextFieldDefaults.MinWidth,
            )
        }

        ReadOnlyDropdown(
            expanded = expandedSearchType,
            setExpanded = { expandedSearchType = it },
            states = SearchType.entries,
            state = currentSearchType,
            setState = setCurrentSearchType,
            label = stringResource(R.string.search_in),
            stringTransform = { it.name },
        )
        ReadOnlyDropdown(
            expanded = expandedListing,
            setExpanded = { expandedListing = it },
            states = ListingType.entries,
            state = currentListing,
            setState = setCurrentListing,
            label = stringResource(R.string.limit_to),
            stringTransform = { getLocalizedListingTypeName(ctx, it) },
        )
    }
}
