package com.jerboa.ui.components.comment.reply

import androidx.compose.ui.focus.FocusManager
import androidx.navigation.NavController
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.CommentResponse
import com.jerboa.datatypes.types.GetPersonDetailsResponse
import com.jerboa.datatypes.types.TestCommentObjects.TEST_COMMENT_RESPONSE
import com.jerboa.datatypes.types.TestCommentObjects.TEST_REPLY_ITEM
import com.jerboa.datatypes.types.TestPersonObjects.TEST_PERSON_VIEW
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
class CommentReplyViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @After
    fun afterTests() {
        unmockkAll()
    }

    private val viewModel = CommentReplyViewModel()

    @Test
    fun testCreateCommentSuccessful() = runTest {
        // Prepare
        mockkStatic("com.jerboa.api.HttpKt")
        every { apiWrapper<CommentResponse>(any()) } returns ApiState.Success(TEST_COMMENT_RESPONSE)
        val givenController = mockk<NavController> {
            every { navigateUp() } returns true
        }
        val givenFocusManager = mockk<FocusManager>() {
            every { clearFocus() } returns Unit
        }
        val givenAccount = mockk<Account>() {
            every { jwt } returns "validjwt"
            every { id } returns TEST_GET_PERSONAL_DETAILS_RESPONSE.person_view.person.id
        }
        val api = mockk<API>() {
            coEvery { createComment(any()) } returns Response.success(TEST_COMMENT_RESPONSE)
        }

        val personalProfileModel = mockk<PersonProfileViewModel>() {
            every { insertComment(TEST_COMMENT_RESPONSE.comment_view) } returns Unit
            every { personDetailsRes } returns ApiState.Success(TEST_GET_PERSONAL_DETAILS_RESPONSE)
        }
        val postViewModel = mockk<PostViewModel>() {
            every { appendComment(TEST_COMMENT_RESPONSE.comment_view) } returns Unit
        }
        // Execute
        viewModel.initialize(TEST_REPLY_ITEM)
        viewModel.createComment(
            content = "New content",
            account = givenAccount,
            navController = givenController,
            focusManager = givenFocusManager,
            api = api,
            postViewModel = postViewModel,
            personProfileViewModel = personalProfileModel,
        )
        advanceUntilIdle()
        // Assert + Verify
        coVerify(exactly = 1) { api.createComment(any()) }
        verify(exactly = 1) { personalProfileModel.insertComment(TEST_COMMENT_RESPONSE.comment_view) }
        verify(exactly = 1) { postViewModel.appendComment(TEST_COMMENT_RESPONSE.comment_view) }

        // Don't insert comment in person profile

        // Prepare
        val givenAccount2 = mockk<Account>() {
            every { jwt } returns "validjwt"
            every { id } returns TEST_GET_PERSONAL_DETAILS_RESPONSE.person_view.person.id + 1
        }
        // Execute
        viewModel.createComment(
            content = "New content",
            account = givenAccount2,
            navController = givenController,
            focusManager = givenFocusManager,
            api = api,
            postViewModel = postViewModel,
            personProfileViewModel = personalProfileModel,
        )
        advanceUntilIdle()
        // Assert + Verify
        coVerify(exactly = 2) { api.createComment(any()) }
        verify(exactly = 1) { personalProfileModel.insertComment(TEST_COMMENT_RESPONSE.comment_view) }
        verify(exactly = 2) { postViewModel.appendComment(TEST_COMMENT_RESPONSE.comment_view) }
    }

    @Test
    fun testCreateCommentUnsuccessful() = runTest {
        // Prepare
        mockkStatic("com.jerboa.api.HttpKt")
        every { apiWrapper<CommentResponse>(any()) } returns ApiState.Failure(
            Exception("some exception"),
        )
        val givenController = mockk<NavController> {
            every { navigateUp() } returns true
        }
        val givenFocusManager = mockk<FocusManager>() {
            every { clearFocus() } returns Unit
        }
        val givenAccount = mockk<Account>() {
            every { jwt } returns "validjwt"
            every { id } returns TEST_GET_PERSONAL_DETAILS_RESPONSE.person_view.person.id
        }
        val api = mockk<API>() {
            coEvery { createComment(any()) } returns Response.success(TEST_COMMENT_RESPONSE)
        }

        val personalProfileModel = mockk<PersonProfileViewModel>() {
            every { insertComment(TEST_COMMENT_RESPONSE.comment_view) } returns Unit
            every { personDetailsRes } returns ApiState.Success(TEST_GET_PERSONAL_DETAILS_RESPONSE)
        }
        val postViewModel = mockk<PostViewModel>() {
            every { appendComment(TEST_COMMENT_RESPONSE.comment_view) } returns Unit
        }
        // Execute
        viewModel.initialize(TEST_REPLY_ITEM)
        viewModel.createComment(
            content = "New content",
            account = givenAccount,
            navController = givenController,
            focusManager = givenFocusManager,
            api = api,
            postViewModel = postViewModel,
            personProfileViewModel = personalProfileModel,
        )
        advanceUntilIdle()
        // Assert + Verify
        coVerify(exactly = 1) { api.createComment(any()) }
        verify(exactly = 0) { personalProfileModel.insertComment(TEST_COMMENT_RESPONSE.comment_view) }
        verify(exactly = 0) { postViewModel.appendComment(TEST_COMMENT_RESPONSE.comment_view) }
    }

    companion object {
        val TEST_GET_PERSONAL_DETAILS_RESPONSE = mockk<GetPersonDetailsResponse>() {
            every { person_view } returns TEST_PERSON_VIEW
        }
    }
}
