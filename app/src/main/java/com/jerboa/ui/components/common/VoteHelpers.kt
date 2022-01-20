package com.jerboa.ui.components.common

import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDownAlt
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.jerboa.VoteType
import com.jerboa.db.Account
import com.jerboa.ui.theme.Muted

@Composable
fun <T> VoteGeneric(
    myVote: Int?,
    votes: Int,
    item: T,
    type: VoteType,
    onVoteClick: (item: T) -> Unit = {},
    showNumber: Boolean = true,
    account: Account?,
) {
    val voteColor =
        when (type) {
            VoteType.Upvote -> upvoteColor(myVote = myVote)
            else -> downvoteColor(myVote = myVote)
        }
    val voteIcon = when (type) {
        VoteType.Upvote -> Icons.Default.ThumbUpAlt
        else -> Icons.Default.ThumbDownAlt
    }

    val votesStr = if (showNumber) {
        if (type == VoteType.Downvote && votes == 0) {
            null
        } else {
            votes.toString()
        }
    } else {
        null
    }

    ActionBarButton(
        onClick = { onVoteClick(item) },
        contentColor = voteColor,
        icon = voteIcon,
        text = votesStr,
        account = account,
    )
}

@Composable
fun upvoteColor(myVote: Int?): Color {
    return when (myVote) {
        1 -> MaterialTheme.colors.secondary
        else -> Muted
    }
}

@Composable
fun downvoteColor(myVote: Int?): Color {
    return when (myVote) {
        -1 -> MaterialTheme.colors.error
        else -> Muted
    }
}
