package com.jerboa.ui.components.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.jerboa.R
import com.jerboa.db.entity.Account
import com.jerboa.feat.VoteType

@Composable
fun VoteGeneric(
    myVote: Int,
    type: VoteType,
    onVoteClick: () -> Unit,
    account: Account,
) {
    val iconAndColor =
        when (type) {
            VoteType.Upvote -> upvoteIconAndColor(myVote = myVote)
            else -> downvoteIconAndColor(myVote = myVote)
        }

    val contentDescription: String =
        if (type == VoteType.Upvote) {
            if (myVote == 1) {
                stringResource(R.string.upvoted)
            } else {
                stringResource(R.string.upvote)
            }
        } else {
            if (myVote == -1) {
                stringResource(R.string.downvoted)
            } else {
                stringResource(R.string.downvote)
            }
        }
    ActionBarButton(
        onClick = onVoteClick,
        contentColor = iconAndColor.second,
        icon = iconAndColor.first,
        contentDescription = contentDescription,
        account = account,
    )
}

@Composable
fun upvoteIconAndColor(myVote: Int?): Pair<ImageVector, Color> =
    when (myVote) {
        1 ->
            Pair(
                ImageVector.vectorResource(id = R.drawable.up_filled),
                scoreColor(myVote = myVote),
            )
        else ->
            Pair(
                ImageVector.vectorResource(id = R.drawable.up_outline),
                MaterialTheme.colorScheme.outline,
            )
    }

@Composable
fun downvoteIconAndColor(myVote: Int?): Pair<ImageVector, Color> =
    when (myVote) {
        -1 ->
            Pair(
                ImageVector.vectorResource(id = R.drawable.down_filled),
                scoreColor(myVote = myVote),
            )
        else ->
            Pair(
                ImageVector.vectorResource(id = R.drawable.down_outline),
                MaterialTheme.colorScheme.outline,
            )
    }
