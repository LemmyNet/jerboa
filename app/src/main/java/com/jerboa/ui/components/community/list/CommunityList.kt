@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.community.list

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.R
import com.jerboa.datatypes.CommunityFollowerView
import com.jerboa.datatypes.CommunitySafe
import com.jerboa.datatypes.CommunityView
import com.jerboa.datatypes.sampleCommunityView
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.community.CommunityLinkLarger
import com.jerboa.ui.components.community.CommunityLinkLargerWithUserCount

@Composable
fun CommunityListHeader(
    navController: NavController = rememberNavController(),
    search: String,
    onSearchChange: (search: String) -> Unit,
) {
    TopAppBar(
        title = {
            CommunityTopBarSearchView(
                search = search,
                onSearchChange = onSearchChange,
            )
        },
        actions = {
            IconButton(
                onClick = { // TODO
                },
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "TODO",
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
            ) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "Back",
                )
            }
        },
    )
}

@Composable
fun CommunityListings(
    communities: List<Any>,
    onClickCommunity: (community: CommunitySafe) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.simpleVerticalScrollbar(listState),
    ) {
        items(
            communities,
            key = { item ->
                when (item) {
                    is CommunityFollowerView -> {
                        item.community.id
                    }

                    is CommunityView -> {
                        item.community.id
                    }

                    else -> {
                        0
                    }
                }
            },
            contentType = { item ->
                when (item) {
                    is CommunityFollowerView -> {
                        item.follower
                    }

                    is CommunityView -> {
                        item.community
                    }

                    else -> {
                        0
                    }
                }
            },

        ) { item ->
            if (item is CommunityFollowerView) {
                CommunityLinkLarger(
                    community = item.community,
                    onClick = onClickCommunity,
                    showDefaultIcon = true,
                )
            } else if (item is CommunityView) {
                CommunityLinkLargerWithUserCount(
                    communityView = item,
                    onClick = onClickCommunity,
                    showDefaultIcon = true,
                )
            }
        }
    }
}

@Preview
@Composable
fun CommunityListingsPreview() {
    val communities = listOf(sampleCommunityView, sampleCommunityView)
    CommunityListings(
        communities = communities,
        onClickCommunity = {},
    )
}

@Composable
fun CommunityTopBarSearchView(
    search: String,
    onSearchChange: (search: String) -> Unit,
) {
    TextField(
        value = search,
        onValueChange = onSearchChange,
        textStyle = MaterialTheme.typography.bodyLarge,
        placeholder = {
            Text(stringResource(R.string.community_list_search))
        },
        modifier = Modifier
            .fillMaxWidth(),
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
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    )
}

@Preview(showBackground = true)
@Composable
fun SearchViewPreview() {
    CommunityTopBarSearchView(
        search = "",
        onSearchChange = {},
    )
}
