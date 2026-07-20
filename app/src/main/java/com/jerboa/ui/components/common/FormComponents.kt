package com.jerboa.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jerboa.R
import com.jerboa.datatypes.data
import it.vercruysse.lemmyapi.dto.SortType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ReadOnlyDropdown(
    expanded: Boolean,
    setExpanded: ((Boolean) -> Unit),
    states: List<T>,
    state: T,
    setState: ((T) -> Unit),
    stringTransform: (T) -> String,
    label: String,
    enabled: Boolean = true,
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) setExpanded(it) },
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            value = stringTransform(state),
            onValueChange = {},
            label = { Text(label) },
            enabled = enabled,
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { setExpanded(false) },
        ) {
            states.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(stringTransform(selectionOption)) },
                    onClick = {
                        setState(selectionOption)
                        setExpanded(false)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    modifier = Modifier.ifDo(state == selectionOption) {
                        this.background(MaterialTheme.colorScheme.onBackground.copy(alpha = .1f))
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortOptionsSelectorField(
    currentSort: SortType,
    setCurrentSort: (SortType) -> Unit,
) {
    var expandedSort by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandedSort,
        onExpandedChange = { expandedSort = it },
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            value = stringResource(currentSort.data.shortForm),
            onValueChange = {},
            label = { Text(stringResource(R.string.selectSort)) },
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedSort) },
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
}
