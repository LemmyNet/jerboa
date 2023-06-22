package com.jerboa.datatypes.types

import com.jerboa.datatypes.types.TestCommentObjects.TEST_COMMENT_VIEW
import com.jerboa.datatypes.types.TestCommunityObjects.TEST_COMMUNITY_VIEW
import com.jerboa.datatypes.types.TestPersonObjects.TEST_PERSON_VIEW
import com.jerboa.datatypes.types.TestPostObjects.TEST_POST_VIEW

object TestSearchObjects {
    val TEST_SEARCH_RESPONSE = SearchResponse(
        type_ = SearchType.Comments,
        comments = listOf(TEST_COMMENT_VIEW),
        users = listOf(TEST_PERSON_VIEW),
        posts = listOf(TEST_POST_VIEW),
        communities = listOf(TEST_COMMUNITY_VIEW),
    )

    val TEST_SEARCH = Search(
        q = "somequery",
        community_id = 1,
        community_name = "cool community",
        creator_id = 4,
        type_ = SearchType.Comments,
        sort = SortType.Active,
        listing_type = ListingType.All,
        page = 1,
        limit = 50,
        auth = "some auth",
    )
}
