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
import com.jerboa.R
import com.jerboa.datatypes.sampleCommunityView
import com.jerboa.feat.BlurNSFW
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.community.CommunityLinkLarger
import com.jerboa.ui.components.community.CommunityLinkLargerWithUserCount
import it.vercruysse.lemmyapi.datatypes.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityListHeader(
    openDrawer: () -> Unit,
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
            // TODO: disabled until ever implemented
            IconButton(
                enabled = false,
                onClick = {
                },
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
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

@Composable
fun CommunityListings(
    communities: List<CommunityView>,
    onClickCommunity: (community: Community) -> Unit,
    modifier: Modifier = Modifier,
    blurNSFW: BlurNSFW,
    showAvatar: Boolean,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.simpleVerticalScrollbar(listState),
    ) {
        items(
            communities,
            key = { it.community.id },
            contentType = { "communitylink" },
        ) { item ->
            // A hack for the community follower views that were coerced into community views without counts
            if (item.counts.users_active_month == 0L) {
                CommunityLinkLarger(
                    community = item.community,
                    onClick = onClickCommunity,
                    showDefaultIcon = true,
                    showAvatar = showAvatar,
                    blurNSFW = blurNSFW,
                )
            } else {
                CommunityLinkLargerWithUserCount(
                    communityView = item,
                    onClick = onClickCommunity,
                    showDefaultIcon = true,
                    showAvatar = showAvatar,
                    blurNSFW = blurNSFW,
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
        blurNSFW = BlurNSFW.NSFW,
        showAvatar = true,
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
        modifier =
            Modifier
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
