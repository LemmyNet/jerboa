package com.jerboa.feat

import it.vercruysse.lemmyapi.datatypes.LocalUserVoteDisplayMode

enum class VoteType(
    val value: Int,
) {
    Upvote(1),
    Downvote(-1),
}

/**
 * This stores live info about votes / scores, in order to update the front end without waiting
 * for an API result
 */
data class InstantScores(
    val myVote: Int,
    val score: Long,
    val upvotes: Long,
    val downvotes: Long,
) {
    fun update(voteAction: VoteType): InstantScores {
        val newVote = newVote(this.myVote, voteAction)
        // get original (up/down)votes, add (up/down)vote if (up/down)voted
        val upvotes = this.upvotes - (if (this.myVote == 1) 1 else 0) + (if (newVote == 1) 1 else 0)
        val downvotes =
            this.downvotes - (if (this.myVote == -1) 1 else 0) + (if (newVote == -1) 1 else 0)

        return InstantScores(
            myVote = newVote,
            upvotes = upvotes,
            downvotes = downvotes,
            score = upvotes - downvotes,
        )
    }

    fun scoreOrPctStr(voteDisplayMode: LocalUserVoteDisplayMode): String? =
        scoreOrPctStr(
            score = score,
            upvotes = upvotes,
            downvotes = downvotes,
            voteDisplayMode = voteDisplayMode,
        )
}

// Set myVote to given action unless it was already set to that action, in which case we reset to 0
fun newVote(
    oldVote: Int,
    voteAction: VoteType,
): Int = if (voteAction.value == oldVote) 0 else voteAction.value

fun upvotePercent(
    upvotes: Long,
    downvotes: Long,
): Float = (upvotes.toFloat() / (upvotes + downvotes))

fun formatPercent(pct: Float): String = "%.0f".format(pct * 100F)

private fun scoreOrPctStr(
    score: Long,
    upvotes: Long,
    downvotes: Long,
    voteDisplayMode: LocalUserVoteDisplayMode,
): String? =
    if (voteDisplayMode.upvote_percentage) {
        formatPercent(upvotePercent(upvotes, downvotes))
    } else if (voteDisplayMode.score || voteDisplayMode.upvotes || voteDisplayMode.downvotes) {
        score.toString()
    } else {
        null
    }

fun LocalUserVoteDisplayMode.Companion.default(score: Boolean? = false) =
    LocalUserVoteDisplayMode(
        local_user_id = -1,
        upvotes = true,
        downvotes = true,
        score = score ?: false,
        upvote_percentage = false,
    )

fun LocalUserVoteDisplayMode.Companion.allHidden() =
    LocalUserVoteDisplayMode(
        local_user_id = -1,
        upvotes = false,
        downvotes = false,
        score = false,
        upvote_percentage = false,
    )
