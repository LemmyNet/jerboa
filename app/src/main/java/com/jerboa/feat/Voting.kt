package com.jerboa.feat

enum class VoteType(val value: Int) {
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
        val downvotes = this.downvotes - (if (this.myVote == -1) 1 else 0) + (if (newVote == -1) 1 else 0)

        return InstantScores(
            myVote = newVote,
            upvotes = upvotes,
            downvotes = downvotes,
            score = upvotes - downvotes,
        )
    }
}

// Set myVote to given action unless it was already set to that action, in which case we reset to 0
fun newVote(
    oldVote: Int,
    voteAction: VoteType,
): Int = if (voteAction.value == oldVote) 0 else voteAction.value
