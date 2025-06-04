package com.jerboa.ui.components.privatemessage

import android.util.Log
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.db.entity.isAnon
import com.jerboa.model.AccountViewModel
import com.jerboa.model.PrivateMessageReplyViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.ActionTopBar
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount
import it.vercruysse.lemmyapi.datatypes.PostId
import it.vercruysse.lemmyapi.datatypes.PrivateMessageView

object PrivateMessage {
    const val PM_VIEW = "private-message::return(pm-view)"
}

@Composable
fun PrivateMessageReplyScreen(
    appState: JerboaAppState,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    onBack: () -> Unit,
    onProfile: (PostId) -> Unit,
) {
    Log.d("jerboa", "got to private message reply screen")
    val privateMessageView = appState.getPrevReturn<PrivateMessageView>(PrivateMessage.PM_VIEW)

    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val privateMessageReplyViewModel: PrivateMessageReplyViewModel = viewModel()

    var reply by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

    val loading =
        when (privateMessageReplyViewModel.createMessageRes) {
            // When message is created, still show loading so that PrivateMessageView is not entered composition
            // again for a brief period, thus requesting focus again and opening keyboard
            ApiState.Loading, is ApiState.Success -> true
            else -> false
        }

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            topBar = {
                ActionTopBar(
                    loading = loading,
                    onBackClick = onBack,
                    onActionClick = {
                        if (!account.isAnon()) {
                            privateMessageReplyViewModel.createPrivateMessage(
                                recipientId = privateMessageView.creator.id,
                                content = reply.text,
                                onGoBack = onBack,
                            )
                        }
                    },
                    title = stringResource(R.string.private_message_reply_reply),
                    actionText = R.string.form_submit,
                    actionIcon = Icons.AutoMirrored.Outlined.Send,
                )
            },
            content = { padding ->
                if (loading) {
                    LoadingBar(padding)
                } else {
                    PrivateMessageReply(
                        privateMessageView = privateMessageView,
                        account = account,
                        reply = reply,
                        onReplyChange = { reply = it },
                        onPersonClick = onProfile,
                        modifier =
                            Modifier
                                .padding(padding)
                                .consumeWindowInsets(padding)
                                .imePadding(),
                        showAvatar = siteViewModel.showAvatar(),
                    )
                }
            },
        )
    }
}
