package com.jerboa.feed

import com.jerboa.datatypes.BanFromCommunityData
import it.vercruysse.lemmyapi.v0x19.datatypes.Person
import it.vercruysse.lemmyapi.v0x19.datatypes.PostView

open class PostController : FeedController<PostView>() {
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
        ) { it.copy(creator = banData.person) }
    }
}
