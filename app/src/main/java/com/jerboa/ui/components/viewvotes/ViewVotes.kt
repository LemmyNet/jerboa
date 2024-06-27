package com.jerboa.ui.components.viewvotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.jerboa.R
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.person.PersonProfileLink
import com.jerboa.ui.theme.MEDIUM_PADDING
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.VoteView

@Composable
fun ViewVotesBody(
    likes: List<VoteView>,
    listState: LazyListState,
    onPersonClick: (personId: PersonId) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier =
            Modifier
                .fillMaxSize()
                .simpleVerticalScrollbar(listState)
                .padding(horizontal = MEDIUM_PADDING),
        verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
    ) {
        items(
            likes,
            key = { like -> like.creator.id },
            contentType = { "like" },
        ) { like ->
            val voteInfo = buildVoteInfo(vote = like.score)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                PersonProfileLink(
                    person = like.creator,
                    onClick = onPersonClick,
                    showAvatar = true,
                    showTags = true,
                    isCommunityBanned = like.creator_banned_from_community,
                )
                Icon(
                    imageVector = voteInfo.icon,
                    tint = voteInfo.color,
                    contentDescription = voteInfo.description,
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = MEDIUM_PADDING))
        }
    }
}

private data class VoteInfo(
    val icon: ImageVector,
    val color: Color,
    val description: String,
)

@Composable
private fun buildVoteInfo(vote: Long): VoteInfo =
    when (vote) {
        1L ->
            VoteInfo(
                ImageVector.vectorResource(id = R.drawable.up_filled),
                MaterialTheme.colorScheme.secondary,
                stringResource(R.string.upvote),
            )

        else ->
            VoteInfo(
                ImageVector.vectorResource(id = R.drawable.down_filled),
                MaterialTheme.colorScheme.error,
                stringResource(R.string.downvote),
            )
    }
