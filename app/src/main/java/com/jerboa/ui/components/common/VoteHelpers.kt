package com.jerboa.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.jerboa.VoteType
import com.jerboa.db.Account
import com.jerboa.ui.theme.muted

@Composable
fun <T> VoteGeneric(
    myVote: Int?,
    votes: Int,
    item: T,
    type: VoteType,
    onVoteClick: (item: T) -> Unit,
    showNumber: Boolean = true,
    account: Account?
) {
    val voteColor =
        when (type) {
            VoteType.Upvote -> upvoteColor(myVote = myVote)
            else -> downvoteColor(myVote = myVote)
        }
    val voteIcon = when (type) {
        VoteType.Upvote -> upvoteIcon(myVote = myVote)
        else -> downvoteIcon(myVote = myVote)
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
        account = account
    )
}

@Composable
fun upvoteColor(myVote: Int?): Color {
    return when (myVote) {
        1 -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onBackground.muted
    }
}

@Composable
fun upvoteIcon(myVote: Int?): ImageVector {
    return when (myVote) {
        1 -> Icons.Outlined.Favorite
        else -> Icons.Outlined.FavoriteBorder
    }
}

@Composable
fun downvoteColor(myVote: Int?): Color {
    return when (myVote) {
        -1 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onBackground.muted
    }
}

fun downvoteIcon(myVote: Int?): ImageVector {
    return when (myVote) {
        -1 -> Icons.Filled.ThumbDown
        else -> Icons.Outlined.ThumbDown
    }
}
