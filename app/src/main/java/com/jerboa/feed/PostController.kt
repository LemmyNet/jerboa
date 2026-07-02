package com.jerboa.feed

import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.nowBoolean
import it.vercruysse.lemmyapi.datatypes.HidePost
import it.vercruysse.lemmyapi.datatypes.Person
import it.vercruysse.lemmyapi.datatypes.PostView

open class PostController : UniqueFeedController<PostView>() {
    fun findAndUpdatePost(updatedPostView: PostView) {
        safeUpdate({ posts ->
            posts.indexOfFirst {
                it.post.id == updatedPostView.post.id
            }
        }) { updatedPostView }
    }

    fun findAndUpdateCreator(person: Person) {
        updateAll(
            { it.indexesOf { postView -> postView.creator.id == person.id } },
        ) { it.copy(creator = person) }
    }

    fun findAndUpdatePostCreatorBannedFromCommunity(banData: BanFromCommunityData) {
        updateAll(
            {
                it.indexesOf { postView ->
                    postView.creator.id == banData.person.id && postView.community.id == banData.community.id
                }
            },
        ) { it.copy(community_actions = it.community_actions?.copy(received_ban_at = nowBoolean(banData.banned)), creator_banned_from_community = banData.banned, creator = banData.person) }
    }

    fun findAndUpdatePostHidden(hidePost: HidePost) {
        updateAll(
            {
                it.indexesOf { postView ->
                    hidePost.post_id == postView.post.id
                }
            },
        ) { it.copy(post_actions = it.post_actions?.copy(hidden_at = nowBoolean(hidePost.hide))) }
    }
}
