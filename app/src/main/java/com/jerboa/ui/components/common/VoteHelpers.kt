package com.jerboa.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.R
import com.jerboa.SHOW_UPVOTE_PCT_THRESHOLD
import com.jerboa.datatypes.sampleInstantScores
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.feat.InstantScores
import com.jerboa.feat.VoteType
import com.jerboa.feat.default
import com.jerboa.feat.formatPercent
import com.jerboa.feat.upvotePercent
import it.vercruysse.lemmyapi.datatypes.LocalUserVoteDisplayMode

@Composable
fun VoteGeneric(
    instantScores: InstantScores,
    voteDisplayMode: LocalUserVoteDisplayMode,
    type: VoteType,
    onVoteClick: () -> Unit,
    account: Account,
    hideScores: Boolean = false,
) {
    val iconAndColor = iconAndColor(type, instantScores)

    val contentDescription = buildContentDescription(type, instantScores)

    val votes = when (type) {
        VoteType.Upvote -> instantScores.upvotes
        VoteType.Downvote -> instantScores.downvotes
    }

    val hideScore = hideScores ||
        when (type) {
            VoteType.Upvote -> !voteDisplayMode.upvotes
            VoteType.Downvote -> !voteDisplayMode.downvotes
        }

    val voteStr = if (votes > 0 && !hideScore) {
        votes.toString()
    } else {
        null
    }

    ActionBarButton(
        onClick = onVoteClick,
        contentColor = iconAndColor.second,
        icon = iconAndColor.first,
        text = voteStr,
        contentDescription = contentDescription,
        account = account,
    )
}

@Composable
@Preview
fun VoteGenericPreview() {
    VoteGeneric(
        instantScores = sampleInstantScores,
        voteDisplayMode = LocalUserVoteDisplayMode.default(),
        type = VoteType.Upvote,
        onVoteClick = { },
        account = AnonAccount,
    )
}

@Composable
fun VoteScore(
    instantScores: InstantScores,
    voteDisplayMode: LocalUserVoteDisplayMode,
    onVoteClick: () -> Unit,
    account: Account,
) {
    val iconAndColor = scoreIconAndColor(myVote = instantScores.myVote)

    val hideScore =
        voteDisplayMode.score && voteDisplayMode.upvotes && instantScores.score == instantScores.upvotes

    if (voteDisplayMode.score && !hideScore) {
        ActionBarButton(
            onClick = onVoteClick,
            contentColor = iconAndColor.second,
            icon = iconAndColor.first,
            text = instantScores.score.toString(),
            contentDescription = stringResource(R.string.score),
            account = account,
        )
    }
}

@Composable
@Preview
fun VoteScorePreview() {
    VoteScore(
        instantScores = sampleInstantScores,
        voteDisplayMode = LocalUserVoteDisplayMode.default().copy(score = true),
        onVoteClick = { },
        account = AnonAccount,
    )
}

@Composable
fun UpvotePercentage(
    instantScores: InstantScores,
    voteDisplayMode: LocalUserVoteDisplayMode,
    account: Account,
) {
    val upvotePct = upvotePercent(
        upvotes = instantScores.upvotes,
        downvotes = instantScores.downvotes,
    )

    if (voteDisplayMode.upvote_percentage && (upvotePct < SHOW_UPVOTE_PCT_THRESHOLD)) {
        ActionBarButton(
            onClick = {},
            noClick = true,
            contentColor = MaterialTheme.colorScheme.outline,
            icon = Icons.Outlined.Percent,
            text = formatPercent(upvotePct),
            contentDescription = stringResource(R.string.upvote_percentage),
            account = account,
        )
    }
}

@Composable
@Preview
fun UpvotePercentagePreview() {
    UpvotePercentage(
        instantScores = sampleInstantScores,
        voteDisplayMode = LocalUserVoteDisplayMode.default().copy(upvote_percentage = true),
        account = AnonAccount,
    )
}

@Composable
private fun buildContentDescription(
    type: VoteType,
    instantScores: InstantScores,
): String =
    if (type == VoteType.Upvote) {
        if (instantScores.myVote == 1) {
            stringResource(R.string.upvoted)
        } else {
            stringResource(R.string.upvote)
        }
    } else {
        if (instantScores.myVote == -1) {
            stringResource(R.string.downvoted)
        } else {
            stringResource(R.string.downvote)
        }
    }

@Composable
private fun iconAndColor(
    type: VoteType,
    instantScores: InstantScores,
): Pair<ImageVector, Color> =
    when (type) {
        VoteType.Upvote -> upvoteIconAndColor(myVote = instantScores.myVote)
        else -> downvoteIconAndColor(myVote = instantScores.myVote)
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

@Composable
fun scoreIconAndColor(myVote: Int?): Pair<ImageVector, Color> =
    when (myVote) {
        1 ->
            Pair(
                Icons.Outlined.Favorite,
                scoreColor(myVote = myVote),
            )

        else ->
            Pair(
                Icons.Outlined.FavoriteBorder,
                scoreColor(myVote = myVote),
            )
    }
