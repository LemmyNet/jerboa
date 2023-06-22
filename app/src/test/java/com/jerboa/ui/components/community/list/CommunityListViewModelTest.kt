package com.jerboa.ui.components.community.list

import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.GetSiteResponse
import com.jerboa.datatypes.types.MyUserInfo
import com.jerboa.datatypes.types.SearchResponse
import com.jerboa.datatypes.types.TestCommunityObjects.TEST_COMMUNITY
import com.jerboa.datatypes.types.TestSearchObjects.TEST_SEARCH
import com.jerboa.datatypes.types.TestSearchObjects.TEST_SEARCH_RESPONSE
import com.jerboa.ui.components.MainDispatcherRule
import com.jerboa.ui.components.home.SiteViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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
class CommunityListViewModelTest {
    private val viewModel = CommunityListViewModel()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun testSetCommunityListFromFollowed() = runTest {
        // Prepare
        mockkStatic("com.jerboa.api.HttpKt")
        every { apiWrapper<SearchResponse>(any()) } returns ApiState.Success(TEST_SEARCH_RESPONSE)

        val api = mockk<API>() {
            coEvery { search(any()) } returns Response.success(TEST_SEARCH_RESPONSE)
        }

        // Execute
        viewModel.searchCommunities(
            form = TEST_SEARCH,
            api = api,
        )
        advanceUntilIdle()

        // Assert & Verify
        when (val res: ApiState<SearchResponse> = viewModel.searchRes) {
            is ApiState.Success -> {
                assertThat(
                    res.data,
                    `is`(TEST_SEARCH_RESPONSE),
                )
            }

            else -> {
                fail()
            }
        }

        // Prepare
        val siteViewModel = SiteViewModel()
        siteViewModel.siteRes = ApiState.Success(
            mockk<GetSiteResponse>() {
                every { my_user } returns TEST_MY_USER_INFO
            },
        )

        // Execute
        viewModel.setCommunityListFromFollowed(siteViewModel)

        // Assert & Verify
        when (val res = viewModel.searchRes) {
            is ApiState.Success -> {
                assertThat(
                    res.data.communities[0].community,
                    `is`(TEST_COMMUNITY),
                )
            }

            else -> {
                fail()
            }
        }
    }

    @After
    fun afterTests() {
        unmockkAll()
    }

    companion object {
        val TEST_MY_USER_INFO = mockk<MyUserInfo>() {
            every { follows } returns listOf(
                mockk() {
                    every { community } returns TEST_COMMUNITY
                },
            )
        }
    }
}
