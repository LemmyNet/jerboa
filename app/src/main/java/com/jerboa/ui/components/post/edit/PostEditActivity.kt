@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.post.edit

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.jerboa.api.uploadPictrsImage
import com.jerboa.db.AccountViewModel
import com.jerboa.imageInputStreamFromUri
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.community.CommunityViewModel
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel
import kotlinx.coroutines.launch

@Composable
fun PostEditActivity(
    accountViewModel: AccountViewModel,
    postEditViewModel: PostEditViewModel,
    navController: NavController,
    postViewModel: PostViewModel,
    personProfileViewModel: PersonProfileViewModel,
    communityViewModel: CommunityViewModel,
    homeViewModel: HomeViewModel
) {
    Log.d("jerboa", "got to post edit activity")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)
    val scope = rememberCoroutineScope()

    val pv = postEditViewModel.postView
    var name by rememberSaveable { mutableStateOf(pv?.post?.name.orEmpty()) }
    var url by rememberSaveable { mutableStateOf(pv?.post?.url.orEmpty()) }
    var body by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                pv?.post?.body.orEmpty()
            )
        )
    }
    var formValid by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        topBar = {
            Column {
                EditPostHeader(
                    navController = navController,
                    formValid = formValid,
                    loading = postEditViewModel.loading,
                    onEditPostClick = {
                        account?.also { acct ->
                            // Clean up that data
                            val nameOut = name.trim()
                            val bodyOut = body.text.trim().ifEmpty { null }
                            val urlOut = url.trim().ifEmpty { null }

                            postEditViewModel.editPost(
                                account = acct,
                                ctx = ctx,
                                body = bodyOut,
                                url = urlOut,
                                name = nameOut,
                                navController = navController,
                                postViewModel = postViewModel,
                                personProfileViewModel = personProfileViewModel,
                                communityViewModel = communityViewModel,
                                homeViewModel = homeViewModel
                            )
                        }
                    }
                )
                if (postEditViewModel.loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
        content = { padding ->
            EditPostBody(
                name = name,
                onNameChange = { name = it },
                body = body,
                onBodyChange = { body = it },
                url = url,
                onUrlChange = { url = it },
                formValid = { formValid = it },
                onPickedImage = { uri ->
                    val imageIs = imageInputStreamFromUri(ctx, uri)
                    scope.launch {
                        account?.also { acct ->
                            url = uploadPictrsImage(acct, imageIs, ctx).orEmpty()
                        }
                    }
                },
                account = account,
                modifier = Modifier.padding(padding)
            )
        }
    )
}
