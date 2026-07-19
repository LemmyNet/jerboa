package com.jerboa.ui.components.search

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.R
import com.jerboa.feat.BlurNSFW
import com.jerboa.ui.components.community.CommunityLinkLarger
import it.vercruysse.lemmyapi.datatypes.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchListHeader(
    openDrawer: () -> Unit,
    search: String,
    onSearchChange: (search: String) -> Unit,
    showSearchOptions: Boolean,
    setShowSearchOptions: (Boolean) -> Unit,
) {
    TopAppBar(
        title = {
            TopBarSearchField(
                search = search,
                onSearchChange = onSearchChange,
            )
        },
        actions = {
            IconButton(
                onClick = { setShowSearchOptions(!showSearchOptions) },
            ) {
                Icon(
                    imageVector = if (showSearchOptions) Icons.Outlined.ExpandMore else Icons.Outlined.ExpandLess,
                    contentDescription = stringResource(R.string.moreOptions),
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    Icons.Outlined.Menu,
                    contentDescription = stringResource(R.string.home_menu),
                )
            }
        },
    )
}

fun LazyListScope.searchCommunityListings(
    communities: List<CommunityView>,
    onClickCommunity: (community: Community) -> Unit,
    blurNSFW: BlurNSFW,
    showAvatar: Boolean,
) {
    items(
        communities,
        contentType = { "communitylink" },
    ) { item ->
        CommunityLinkLarger(
            community = item.community,
            onClick = onClickCommunity,
            showDefaultIcon = true,
            blurNSFW = blurNSFW,
            showAvatar = showAvatar,
            // A hack for the community follower views that were coerced into community views without counts
            usersPerMonth =
                if (item.counts.users_active_month == 0L) {
                    null
                } else {
                    item.counts.users_active_month
                },
        )
        HorizontalDivider()
    }
}

@Composable
fun TopBarSearchField(
    search: String,
    onSearchChange: (search: String) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    TextField(
        value = search,
        onValueChange = onSearchChange,
        textStyle = MaterialTheme.typography.bodyLarge,
        placeholder = {
            Text(stringResource(R.string.community_list_search))
        },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            if (search.isNotEmpty()) {
                IconButton(
                    onClick = { onSearchChange("") },
                ) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "",
                    )
                }
            }
        },
        singleLine = true,
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { focusManager.clearFocus() },
        ),
    )
}

@Preview(showBackground = true)
@Composable
fun SearchViewPreview() {
    TopBarSearchField(
        search = "",
        onSearchChange = {},
    )
}
