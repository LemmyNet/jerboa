package com.jerboa.ui.components.view_votes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
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
import it.vercruysse.lemmyapi.v0x19.datatypes.VoteView

@Composable
fun ViewVotesBody(
    likes: List<VoteView>,
    listState: LazyListState,
    padding: PaddingValues,
    onPersonClick: (personId: Int) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier =
            Modifier
                .fillMaxSize()
                .simpleVerticalScrollbar(listState)
                .padding(
                    vertical = padding.calculateTopPadding(),
                    horizontal = MEDIUM_PADDING,
                ),
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
                )
                Icon(
                    imageVector = voteInfo.icon,
                    tint = voteInfo.color,
                    contentDescription = voteInfo.description,
                )
            }
            Divider(modifier = Modifier.padding(vertical = MEDIUM_PADDING))
        }
    }
}

private data class VoteInfo(
    val icon: ImageVector,
    val color: Color,
    val description: String,
)

@Composable
private fun buildVoteInfo(vote: Int): VoteInfo {
    return when (vote) {
        1 ->
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
}
