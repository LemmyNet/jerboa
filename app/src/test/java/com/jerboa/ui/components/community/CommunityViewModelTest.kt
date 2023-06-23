package com.jerboa.ui.components.community

import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.datatypes.types.GetPostsResponse
import com.jerboa.datatypes.types.PostResponse
import com.jerboa.datatypes.types.TestCommunityObjects.TEST_COMMUNITY
import com.jerboa.datatypes.types.TestCommunityObjects.TEST_COMMUNITY_RESPONSE
import com.jerboa.datatypes.types.TestCommunityObjects.TEST_COMMUNITY_VIEW
import com.jerboa.datatypes.types.TestCommunityObjects.TEST_FOLLOW_COMMUNITY
import com.jerboa.datatypes.types.TestCommunityObjects.TEST_GET_COMMUNITY_RESPONSE
import com.jerboa.datatypes.types.TestPostObjects.TEST_GET_POSTS
import com.jerboa.datatypes.types.TestPostObjects.TEST_GET_POSTS_RESPONSE
import com.jerboa.datatypes.types.TestPostObjects.TEST_POST
import com.jerboa.datatypes.types.TestPostObjects.TEST_POST_RESPONSE
import com.jerboa.datatypes.types.TestPostObjects.TEST_POST_VIEW
import com.jerboa.ui.components.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import junitparams.JUnitParamsRunner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnitParamsRunner::class)
class CommunityViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @After
    fun afterTests() {
        unmockkAll()
    }

    @Test
    fun testPage() {
        val viewModel = CommunityViewModel()

        viewModel.nextPage()
        viewModel.nextPage()
        viewModel.nextPage()
        assertThat(
            viewModel.page,
            `is`(4),
        )

        viewModel.resetPage()
        assertThat(
            viewModel.page,
            `is`(1),
        )
    }

    @Test
    fun testAppendSamePosts() = runTest {
        // Prepare
        val viewModel = CommunityViewModel()
        val api = mockk<API>() {
            coEvery { getPosts(any()) } returns Response.success(TEST_GET_POSTS_RESPONSE)
        }
        // Execute
        viewModel.getPosts(
            TEST_GET_POSTS,
            api,
        )

        viewModel.appendPosts(
            TEST_GET_POSTS,
            api,
        )
        advanceUntilIdle()
        // Assert + Verify
        when (val pRes = viewModel.postsRes) {
            is ApiState.Success -> {
                assertThat(
                    pRes.data,
                    `is`(TEST_GET_POSTS_RESPONSE),
                )
            }

            else -> {
                fail()
            }
        }
    }

    @Test
    fun testAppendDifferentPosts() = runTest {
        // Prepare
        val viewModel = CommunityViewModel()
        val api = mockk<API>() {
            coEvery { getPosts(any()) } returns Response.success(TEST_GET_POSTS_RESPONSE)
        }
        // Execute
        viewModel.getPosts(
            TEST_GET_POSTS,
            api,
        )

        // Prepare
        val appendPostView = TEST_POST_VIEW.copy(
            post = TEST_POST.copy(
                id = 999,
            ),
        )
        val secondPostResponse = TEST_GET_POSTS_RESPONSE.copy(
            posts = listOf(
                appendPostView,
            ),
        )
        val api2 = mockk<API>() {
            coEvery { getPosts(any()) } returns Response.success(secondPostResponse)
        }

        // Execute
        viewModel.appendPosts(
            TEST_GET_POSTS,
            api2,
        )
        advanceUntilIdle()
        // Assert + Verify
        when (val pRes = viewModel.postsRes) {
            is ApiState.Success -> {
                assertThat(
                    pRes.data,
                    `is`(
                        GetPostsResponse(
                            listOf(
                                TEST_GET_POSTS_RESPONSE.posts[0],
                                appendPostView,
                            ),
                        ),
                    ),
                )
            }

            else -> {
                fail()
            }
        }
    }

    @Test
    fun testFollowCommunity() = runTest {
        // 1. Set initial Community
        // Prepare
        val viewModel = CommunityViewModel()
        val expectedFollowCommunity = TEST_COMMUNITY_VIEW.copy(
            community = TEST_COMMUNITY.copy(id = 99),
        )
        val api = mockk<API>() {
            coEvery { followCommunity(any()) } returns Response.success(
                TEST_COMMUNITY_RESPONSE.copy(
                    community_view = expectedFollowCommunity,
                ),
            )
            coEvery { getCommunity(any()) } returns Response.success(
                TEST_GET_COMMUNITY_RESPONSE,
            )
        }
        // Execute
        viewModel.getCommunity(
            mockk(),
            api,
        )

        // 2. Follow community
        // Execute
        viewModel.followCommunity(
            TEST_FOLLOW_COMMUNITY,
            api,
        )
        advanceUntilIdle()
        // Assert & Verify
        when (val comRes = viewModel.communityRes) {
            is ApiState.Success -> {
                assertThat(
                    comRes.data,
                    `is`(
                        TEST_GET_COMMUNITY_RESPONSE.copy(
                            community_view = expectedFollowCommunity,
                        ),
                    ),
                )
            }

            else -> {
                fail()
            }
        }
    }

    @Test
    fun testLikePost() = runTest {
        // 1. Set initial Posts
        // Prepare
        val viewModel = CommunityViewModel()
        val expectedLikedPost: PostResponse = TEST_POST_RESPONSE.copy(
            post_view = TEST_POST_VIEW.copy(
                my_vote = 6,
            ),
        )
        val api = mockk<API>() {
            coEvery { likePost(any()) } returns Response.success(
                expectedLikedPost,
            )
            coEvery { getPosts(any()) } returns Response.success(
                TEST_GET_POSTS_RESPONSE,
            )
        }
        // Execute
        viewModel.getPosts(
            mockk(),
            api,
        )
        advanceUntilIdle()

        // 2. Like Post
        viewModel.likePost(
            mockk(),
            api,
        )
        advanceUntilIdle()
        // Assert & Verify
        when (val postRes: ApiState<GetPostsResponse> = viewModel.postsRes) {
            is ApiState.Success -> {
                assertThat(
                    postRes.data,
                    `is`(
                        TEST_GET_POSTS_RESPONSE.copy(
                            listOf(expectedLikedPost.post_view),
                        ),
                    ),
                )
            }

            else -> {
                fail()
            }
        }
    }

    @Test
    fun testSavePost() = runTest {
        // 1. Set initial Posts
        // Prepare
        val viewModel = CommunityViewModel()
        val expectedSavedPost: PostResponse = TEST_POST_RESPONSE
        val api = mockk<API>() {
            coEvery { savePost(any()) } returns Response.success(
                expectedSavedPost,
            )
            coEvery { getPosts(any()) } returns Response.success(
                TEST_GET_POSTS_RESPONSE.copy(
                    listOf(
                        TEST_POST_VIEW.copy(
                            saved = false,
                        ),
                    ),
                ),
            )
        }
        // Execute
        viewModel.getPosts(
            mockk(),
            api,
        )
        advanceUntilIdle()

        // 2. Save Post
        viewModel.savePost(
            mockk(),
            api,
        )
        advanceUntilIdle()
        // Assert & Verify
        when (val postRes: ApiState<GetPostsResponse> = viewModel.postsRes) {
            is ApiState.Success -> {
                assertThat(
                    postRes.data,
                    `is`(
                        TEST_GET_POSTS_RESPONSE,
                    ),
                )
            }

            else -> {
                fail()
            }
        }
    }

    @Test
    fun testDeletePost() = runTest {
        // 1. Set initial Posts
        // Prepare
        val viewModel = CommunityViewModel()
        val expectedDeletedPost: PostResponse = TEST_POST_RESPONSE.copy(
            post_view = TEST_POST_VIEW.copy(
                post = TEST_POST.copy(
                    deleted = true,
                ),
            ),
        )
        val api = mockk<API>() {
            coEvery { deletePost(any()) } returns Response.success(
                expectedDeletedPost,
            )
            coEvery { getPosts(any()) } returns Response.success(
                TEST_GET_POSTS_RESPONSE,
            )
        }
        // Execute
        viewModel.getPosts(
            mockk(),
            api,
        )
        advanceUntilIdle()

        // 2. Delete Post
        viewModel.deletePost(
            mockk(),
            api,
        )
        advanceUntilIdle()
        // Assert & Verify
        when (val postRes: ApiState<GetPostsResponse> = viewModel.postsRes) {
            is ApiState.Success -> {
                assertThat(
                    postRes.data,
                    `is`(
                        TEST_GET_POSTS_RESPONSE.copy(
                            listOf(expectedDeletedPost.post_view),
                        ),
                    ),
                )
            }

            else -> {
                fail()
            }
        }
    }
}
