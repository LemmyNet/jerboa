package com.jerboa.ui.components.comment.edit

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.jerboa.api.ApiState
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentEditActivity(
    accountViewModel: AccountViewModel,
    navController: NavController,
    commentEditViewModel: CommentEditViewModel,
    personProfileViewModel: PersonProfileViewModel,
    postViewModel: PostViewModel,
) {
    Log.d("jerboa", "got to comment edit activity")

    val account = getCurrentAccount(accountViewModel = accountViewModel)

    var content by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(commentEditViewModel.commentView.value?.comment?.content.orEmpty())) }

    val loading = when (commentEditViewModel.editCommentRes) {
        ApiState.Loading -> true
        else -> false
    }

    val focusManager = LocalFocusManager.current

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            topBar = {
                CommentEditHeader(
                    navController = navController,
                    loading = loading,
                    onSaveClick = {
                        account?.also { acct ->
                            commentEditViewModel.editComment(
                                content = content.text,
                                navController = navController,
                                focusManager = focusManager,
                                account = acct,
                                personProfileViewModel = personProfileViewModel,
                                postViewModel = postViewModel,
                            )
                        }
                    },
                )
            },
            content = { padding ->
                CommentEdit(
                    content = content,
                    account = account,
                    onContentChange = { content = it },
                    padding = padding,
                )
            },
        )
    }
}
