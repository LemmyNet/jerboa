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
import com.jerboa.datatypes.sampleLocalSite
import com.jerboa.datatypes.sampleLocalUser
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.feat.InstantScores
import com.jerboa.feat.PostOrCommentType
import com.jerboa.feat.VoteType
import it.vercruysse.lemmyapi.datatypes.LocalSite
import it.vercruysse.lemmyapi.datatypes.LocalUser
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.enums.FederationMode
import it.vercruysse.lemmyapi.enums.VoteShow

@Composable
fun VoteGeneric(
    instantScores: InstantScores,
    localUser: LocalUser?,
    localSite: LocalSite,
    voteContentType: PostOrCommentType,
    voteType: VoteType,
    /**
     * The creator ID is necessary for hiding downvotes to yourself
     */
    creatorId: PersonId,
    onVoteClick: () -> Unit,
    account: Account,
) {
    val iconAndColor = iconAndColor(voteType, instantScores)

    val contentDescription = buildContentDescription(voteType, instantScores)

    data class VoteData(
        val votes: Long,
        val enabled: Boolean,
        val show: Boolean,
    )

    val voteData = when (voteType) {
        VoteType.Upvote -> {
            VoteData(
                instantScores.upvotes,
                enableUpvotes(localSite, voteContentType),
                showUpvotes(localUser, localSite, voteContentType) && instantScores.upvotes != 0L,
            )
        }

        VoteType.Downvote -> {
            VoteData(
                instantScores.downvotes,
                enableDownvotes(localSite, voteContentType),
                showDownvotes(localUser, localSite, voteContentType, creatorId) && instantScores.downvotes != 0L,
            )
        }
    }

    val voteStr = if (voteData.show) {
        voteData.votes.toString()
    } else {
        null
    }

    if (voteData.enabled) {
        ActionBarButton(
            onClick = onVoteClick,
            contentColor = iconAndColor.second,
            icon = iconAndColor.first,
            text = voteStr,
            contentDescription = contentDescription,
            account = account,
        )
    }
}

@Composable
@Preview
fun VoteGenericPreview() {
    VoteGeneric(
        instantScores = sampleInstantScores,
        localUser = sampleLocalUser,
        localSite = sampleLocalSite,
        voteContentType = PostOrCommentType.Post,
        voteType = VoteType.Upvote,
        onVoteClick = { },
        creatorId = 0,
        account = AnonAccount,
    )
}

@Composable
fun VoteScore(
    instantScores: InstantScores,
    localUser: LocalUser?,
    localSite: LocalSite,
    voteContentType: PostOrCommentType,
    onVoteClick: () -> Unit,
    account: Account,
) {
    val iconAndColor = scoreIconAndColor(myVote = instantScores.myVote)

    val showUpvotes = showUpvotes(localUser, localSite, voteContentType)
    val showScore = showScore(localUser)
    val enabled = enableUpvotes(localSite, voteContentType)

    // If the score is the same as the upvotes,
    // and both score and upvotes are enabled,
    // only show the upvotes.
    val hideScore = !showScore || (showUpvotes && instantScores.score == instantScores.upvotes)

    val scoreStr = if (showScore && !hideScore) {
        instantScores.score.toString()
    } else {
        null
    }

    if (enabled && !hideScore) {
        ActionBarButton(
            onClick = onVoteClick,
            contentColor = iconAndColor.second,
            icon = iconAndColor.first,
            text = scoreStr,
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
        localUser = sampleLocalUser,
        localSite = sampleLocalSite,
        voteContentType = PostOrCommentType.Post,
        onVoteClick = { },
        account = AnonAccount,
    )
}

@Composable
fun UpvotePercentage(
    instantScores: InstantScores,
    localUser: LocalUser?,
    localSite: LocalSite,
    voteContentType: PostOrCommentType,
    account: Account,
) {
    val upvotePct = upvotePercent(
        upvotes = instantScores.upvotes,
        downvotes = instantScores.downvotes,
    )

    val showPct = showPercentage(localUser, localSite, voteContentType) &&
        (upvotePct < SHOW_UPVOTE_PCT_THRESHOLD)

    if (showPct) {
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
        localUser = sampleLocalUser,
        localSite = sampleLocalSite,
        voteContentType = PostOrCommentType.Post,
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
        1 -> {
            Pair(
                ImageVector.vectorResource(id = R.drawable.up_filled),
                scoreColor(myVote = myVote),
            )
        }

        else -> {
            Pair(
                ImageVector.vectorResource(id = R.drawable.up_outline),
                MaterialTheme.colorScheme.outline,
            )
        }
    }

@Composable
fun downvoteIconAndColor(myVote: Int?): Pair<ImageVector, Color> =
    when (myVote) {
        -1 -> {
            Pair(
                ImageVector.vectorResource(id = R.drawable.down_filled),
                scoreColor(myVote = myVote),
            )
        }

        else -> {
            Pair(
                ImageVector.vectorResource(id = R.drawable.down_outline),
                MaterialTheme.colorScheme.outline,
            )
        }
    }

@Composable
fun scoreIconAndColor(myVote: Int?): Pair<ImageVector, Color> =
    when (myVote) {
        1 -> {
            Pair(
                Icons.Outlined.Favorite,
                scoreColor(myVote = myVote),
            )
        }

        else -> {
            Pair(
                Icons.Outlined.FavoriteBorder,
                scoreColor(myVote = myVote),
            )
        }
    }

private fun showUpvotes(
    localUser: LocalUser?,
    localSite: LocalSite,
    type: PostOrCommentType,
): Boolean = enableUpvotes(localSite, type) && (localUser?.show_upvotes ?: true)

private fun showDownvotes(
    localUser: LocalUser?,
    localSite: LocalSite,
    type: PostOrCommentType,
    creatorId: PersonId,
): Boolean {
    val show = localUser?.show_downvotes === VoteShow.Show ||
        (localUser?.show_downvotes === VoteShow.ShowForOthers && localUser.person_id != creatorId)
    return enableDownvotes(localSite, type) && show
}

private fun showScore(localUser: LocalUser?): Boolean = localUser?.show_score ?: false

private fun showPercentage(
    localUser: LocalUser?,
    localSite: LocalSite,
    type: PostOrCommentType,
): Boolean = localUser?.show_upvote_percentage ?: true && enableUpvotes(localSite, type) && enableDownvotes(localSite, type)

private fun enableDownvotes(
    localSite: LocalSite,
    type: PostOrCommentType,
): Boolean =
    when (type) {
        PostOrCommentType.Post -> localSite.post_downvotes !== FederationMode.Disable
        PostOrCommentType.Comment -> localSite.comment_downvotes !== FederationMode.Disable
    }

private fun enableUpvotes(
    localSite: LocalSite,
    type: PostOrCommentType,
): Boolean =
    when (type) {
        PostOrCommentType.Post -> localSite.post_upvotes !== FederationMode.Disable
        PostOrCommentType.Comment -> localSite.comment_upvotes !== FederationMode.Disable
    }

private fun upvotePercent(
    upvotes: Long,
    downvotes: Long,
): Float = (upvotes.toFloat() / (upvotes + downvotes))

private fun formatPercent(pct: Float): String = "%.0f".format(pct * 100F)

private fun scoreOrPctStr(
    localUser: LocalUser?,
    localSite: LocalSite,
    type: PostOrCommentType,
    score: Long,
    upvotes: Long,
    downvotes: Long,
): String? =
    if (showScore(localUser)) {
        score.toString()
    } else if (showPercentage(localUser, localSite, type)) {
        formatPercent(upvotePercent(upvotes, downvotes))
    } else {
        null
    }

fun InstantScores.scoreOrPctStr(
    localUser: LocalUser?,
    localSite: LocalSite,
    type: PostOrCommentType,
): String? =
    scoreOrPctStr(
        localUser = localUser,
        localSite = localSite,
        type = type,
        score = this.score,
        upvotes = this.upvotes,
        downvotes = this.downvotes,
    )
