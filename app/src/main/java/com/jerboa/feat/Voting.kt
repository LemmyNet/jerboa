package com.jerboa.feat

import it.vercruysse.lemmyapi.enums.VoteAction

enum class PostOrCommentType {
    Post,
    Comment,
}

/**
 * This stores live info about votes / scores, in order to update the front end without waiting
 * for an API result
 */
data class InstantScores(
    val myVote: VoteAction?,
    val score: Long,
    val upvotes: Long,
    val downvotes: Long,
) {
    fun update(voteAction: VoteAction): InstantScores {
        val newVote = newVote(this.myVote, voteAction)

        // get original (up/down)votes, add (up/down)vote if (up/down)voted

        val upvotes = this.upvotes - (if (this.myVote == VoteAction.UpVote) 1 else 0) + (if (newVote == VoteAction.UpVote) 1 else 0)
        val downvotes =
            this.downvotes - (if (this.myVote == VoteAction.DownVote) 1 else 0) + (if (newVote == VoteAction.DownVote) 1 else 0)

        return InstantScores(
            myVote = newVote,
            upvotes = upvotes,
            downvotes = downvotes,
            score = upvotes - downvotes,
        )
    }
}

// Set myVote to given action unless it was already set to that action, in which case we reset to 0
// TODO this should really be private to the vote button components
fun newVote(
    oldVote: VoteAction?,
    voteAction: VoteAction,
): VoteAction = if (voteAction == oldVote) VoteAction.NoVote else voteAction
