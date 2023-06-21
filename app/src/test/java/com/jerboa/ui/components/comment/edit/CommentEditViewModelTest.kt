package com.jerboa.ui.components.comment.edit

import androidx.compose.ui.focus.FocusManager
import androidx.navigation.NavController
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.CommentResponse
import com.jerboa.db.Account
import com.jerboa.ui.components.MainDispatcherRule
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import junitparams.JUnitParamsRunner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnitParamsRunner::class)
class CommentEditViewModelTest {
    private val viewModel = CommentEditViewModel()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @After
    fun afterTests() {
        unmockkAll()
    }

    @Test
    fun testEditCommentSuccessful() = runTest {
        // Prepare
        val expectedResponse = TEST_COMMENT_RESPONSE
        mockkStatic("com.jerboa.api.HttpKt")
        every { apiWrapper<CommentResponse>(any()) } returns ApiState.Success(expectedResponse)

        val newContent = "Some new stuff"
        val givenController = mockk<NavController> {
            every { navigateUp() } returns true
        }
        val givenAccount = mockk<Account> {
            every { jwt } returns "some valid jwt"
        }
        val givenFocusManager = mockk<FocusManager>() {
            every { clearFocus() } returns Unit
        }
        val personalProfileModel = mockk<PersonProfileViewModel>() {
            every { updateComment(expectedResponse.comment_view) } returns Unit
        }
        val postViewModel = mockk<PostViewModel>() {
            every { updateComment(expectedResponse.comment_view) } returns Unit
        }
        val api = mockk<API>() {
            coEvery { editComment(any()) } returns Response.success(expectedResponse)
        }
        // Execute
        viewModel.initialize(expectedResponse.comment_view)
        viewModel.editComment(
            newContent,
            givenController,
            givenFocusManager,
            givenAccount,
            personalProfileModel,
            postViewModel,
            api,
        )
        advanceUntilIdle()

        // Assert + Verify
        coVerify(exactly = 1) { api.editComment(any()) }
        verify(exactly = 1) { personalProfileModel.updateComment(TEST_COMMENT_RESPONSE.comment_view) }
        verify(exactly = 1) { postViewModel.updateComment(TEST_COMMENT_RESPONSE.comment_view) }
    }

    @Test
    fun testEditCommentUnsuccessful() = runTest {
        // Prepare
        val expectedResponse = TEST_COMMENT_RESPONSE
        mockkStatic("com.jerboa.api.HttpKt")
        every { apiWrapper<CommentResponse>(any()) } returns ApiState.Failure(
            Exception("some exception"),
        )

        val newContent = "Some new stuff"
        val givenController = mockk<NavController> {
            every { navigateUp() } returns true
        }
        val givenAccount = mockk<Account> {
            every { jwt } returns "some valid jwt"
        }
        val givenFocusManager = mockk<FocusManager>() {
            every { clearFocus() } returns Unit
        }
        val personalProfileModel = mockk<PersonProfileViewModel>() {
            every { updateComment(expectedResponse.comment_view) } returns Unit
        }
        val postViewModel = mockk<PostViewModel>() {
            every { updateComment(expectedResponse.comment_view) } returns Unit
        }
        val api = mockk<API>() {
            coEvery { editComment(any()) } returns Response.success(expectedResponse)
        }
        // Execute
        viewModel.initialize(expectedResponse.comment_view)
        viewModel.editComment(
            newContent,
            givenController,
            givenFocusManager,
            givenAccount,
            personalProfileModel,
            postViewModel,
            api,
        )
        advanceUntilIdle()

        // Assert + Verify
        coVerify(exactly = 1) { api.editComment(any()) }
        verify(exactly = 0) { personalProfileModel.updateComment(TEST_COMMENT_RESPONSE.comment_view) }
        verify(exactly = 0) { postViewModel.updateComment(TEST_COMMENT_RESPONSE.comment_view) }
    }

    companion object {
        val TEST_COMMENT_RESPONSE = CommentResponse(
            comment_view = mockk() {
                every { comment } returns mockk() {
                    every { id } returns 1
                }
            },
            recipient_ids = emptyList(),
            form_id = null,
        )
    }
}
