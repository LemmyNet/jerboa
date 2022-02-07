package com.jerboa.ui.components.community.list

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.datatypes.CommunityFollowerView
import com.jerboa.datatypes.CommunitySafe
import com.jerboa.datatypes.CommunityView
import com.jerboa.datatypes.sampleCommunityView
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.community.CommunityLinkLarger
import com.jerboa.ui.components.community.CommunityLinkLargerWithUserCount
import com.jerboa.ui.theme.APP_BAR_ELEVATION

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
        elevation = APP_BAR_ELEVATION,
        actions = {
            IconButton(
                onClick = { // TODO
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "TODO"
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Back"
                )
            }
        },
    )
}

@Composable
fun CommunityListings(
    communities: List<Any>,
    onClickCommunity: (community: CommunitySafe) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.simpleVerticalScrollbar(listState),
    ) {
        items(communities) { item ->
            if (item is CommunityFollowerView) {
                CommunityLinkLarger(
                    community = item.community,
                    onClick = onClickCommunity,
                )
            } else if (item is CommunityView) {
                CommunityLinkLargerWithUserCount(
                    communityView = item,
                    onClick = onClickCommunity,
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
        textStyle = MaterialTheme.typography.body1,
        placeholder = {
            Text("Search...")
        },
        modifier = Modifier
            .fillMaxWidth(),
        trailingIcon = {
            if (search.isNotEmpty()) {
                IconButton(
                    onClick = { onSearchChange("") }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                    )
                }
            }
        },
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
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
