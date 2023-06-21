package com.jerboa.datatypes.types

import com.jerboa.datatypes.types.TestCommunityObjects.TEST_COMMUNITY
import com.jerboa.datatypes.types.TestPersonObjects.TEST_PERSON
import com.jerboa.datatypes.types.TestPostObjects.TEST_POST
import com.jerboa.ui.components.comment.reply.ReplyItem

object TestCommentObjects {
    val TEST_COMMENT_REPLY = CommentReply(
        id = 1,
        recipient_id = 11,
        comment_id = 50,
        read = true,
        published = "yes",
    )
    val TEST_COMMENT = Comment(
        id = 1,
        creator_id = 25,
        post_id = 1000,
        content = "some cool content",
        removed = false,
        published = "yes",
        updated = "yes",
        deleted = false,
        ap_id = "apid",
        local = true,
        path = "somepath",
        distinguished = false,
        language_id = 2,
    )

    val TEST_COMMENT_AGGREGATES = CommentAggregates(
        id = 23,
        comment_id = 12,
        score = 56,
        upvotes = 100,
        downvotes = 1,
        published = "yes",
        child_count = 0,
    )

    val TEST_COMMENT_REPLY_VIEW: CommentReplyView =
        CommentReplyView(
            comment_reply = TEST_COMMENT_REPLY,
            comment = TEST_COMMENT,
            creator = TEST_PERSON,
            post = TEST_POST,
            community = TEST_COMMUNITY,
            recipient = TEST_PERSON.copy(
                id = 5,
                display_name = "wizzö",
                name = "Phil Wizzö Campbell",
            ),
            counts = TEST_COMMENT_AGGREGATES,
            creator_banned_from_community = false,
            saved = true,
            subscribed = SubscribedType.Subscribed,
            creator_blocked = false,
            my_vote = 1,
        )

    val TEST_COMMENT_RESPONSE = CommentResponse(
        recipient_ids = listOf(1),
        form_id = "formId",
        comment_view = CommentView(
            comment = TEST_COMMENT,
            creator = TEST_PERSON,
            post = TEST_POST,
            community = TEST_COMMUNITY,
            counts = TEST_COMMENT_AGGREGATES,
            creator_banned_from_community = false,
            subscribed = SubscribedType.Subscribed,
            saved = true,
            creator_blocked = false,
            my_vote = 1,
        ),
    )

    val TEST_REPLY_ITEM: ReplyItem =
        ReplyItem.CommentReplyItem(TEST_COMMENT_REPLY_VIEW)
}
