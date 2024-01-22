package com.jerboa.ui.components.view_votes.post

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.db.entity.isAnon
import com.jerboa.model.AccountViewModel
import com.jerboa.model.CreateReportViewModel
import com.jerboa.model.PostLikesViewModel
import com.jerboa.ui.components.common.ActionTopBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.report.CreateReportBody
import it.vercruysse.lemmyapi.v0x19.datatypes.PostId

@Composable
fun PostLikesActivity(
    postId: PostId,
    onBack: () -> Unit,
) {
    Log.d("jerboa", "got to post likes activity")

    val ctx = LocalContext.current

    val postLikesViewModel: PostLikesViewModel = viewModel()

    val focusManager = LocalFocusManager.current
    val loading =
        when (postLikesViewModel.postLikesRes) {
            ApiState.Loading -> true
            else -> false
        }

    // TODO do scrolling

    Scaffold(
        topBar = {
            ActionTopBar(
                title = stringResource(R.string.votes),
                loading = loading,
                onBackClick = onBack,
                onActionClick = {},
            )
        },
        content = { padding ->
            when ...
            ViewVotesBody(
                votes =

                padding = padding,
            )
        },
    )
}
