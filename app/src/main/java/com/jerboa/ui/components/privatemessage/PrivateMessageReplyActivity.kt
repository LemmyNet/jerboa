@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.privatemessage

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.jerboa.datatypes.api.CreatePrivateMessage
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.inbox.InboxViewModel

@Composable
fun PrivateMessageReplyActivity(
    inboxViewModel: InboxViewModel,
    accountViewModel: AccountViewModel,
    navController: NavController,
    siteViewModel: SiteViewModel,
) {
    Log.d("jerboa", "got to private message reply activity")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)

    var reply by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

    val focusManager = LocalFocusManager.current

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            topBar = {
                PrivateMessageReplyHeader(
                    navController = navController,
                    loading = inboxViewModel.privateMessageReplyLoading.value,
                    onSendClick = {
                        account?.also { account ->
                            inboxViewModel.replyToPrivateMessageView?.also { privateMessageView ->
                                val recipientId = privateMessageView.creator.id
                                val form =
                                    CreatePrivateMessage(
                                        content = reply.text,
                                        recipient_id = recipientId,
                                        auth = account.jwt,
                                    )
                                inboxViewModel.createPrivateMessage(
                                    form,
                                    ctx,
                                    navController,
                                    focusManager,
                                )
                            }
                        }
                    },
                )
            },
            content = { padding ->
                if (inboxViewModel.privateMessageReplyLoading.value) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    inboxViewModel.replyToPrivateMessageView?.also { privateMessageView ->
                        PrivateMessageReply(
                            privateMessageView = privateMessageView,
                            account = account,
                            reply = reply,
                            onReplyChange = { reply = it },
                            onPersonClick = { personId ->
                                navController.navigate(route = "profile/$personId")
                            },
                            modifier = Modifier
                                .padding(padding)
                                .imePadding(),
                            showAvatar = siteViewModel.siteRes?.my_user?.local_user_view?.local_user?.show_avatars ?: true,
                        )
                    }
                }
            },
        )
    }
}
