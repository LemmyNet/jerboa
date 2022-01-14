package com.jerboa.ui.components.private_message

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavController
import com.jerboa.datatypes.api.CreatePrivateMessage
import com.jerboa.db.AccountViewModel
import com.jerboa.getCurrentAccount
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.person.personClickWrapper
import com.jerboa.ui.components.post.InboxViewModel

@Composable
fun PrivateMessageReplyActivity(
    inboxViewModel: InboxViewModel,
    accountViewModel: AccountViewModel,
    personProfileViewModel: PersonProfileViewModel,
    navController: NavController,
) {

    Log.d("jerboa", "got to private message reply activity")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)

    var reply by rememberSaveable { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            topBar = {
                PrivateMessageReplyHeader(
                    navController = navController,
                    onSendClick = {
                        account?.also { account ->
                            inboxViewModel.replyToPrivateMessageView?.also { privateMessageView ->
                                val recipientId = privateMessageView.creator.id
                                val form =
                                    CreatePrivateMessage(
                                        content = reply,
                                        recipient_id = recipientId,
                                        auth = account.jwt
                                    )
                                inboxViewModel.createPrivateMessage(
                                    form,
                                    ctx, navController,
                                    focusManager
                                )
                            }
                        }
                    }
                )
            },
            content = {
                if (inboxViewModel.privateMessageReplyLoading.value) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    inboxViewModel.replyToPrivateMessageView?.also { privateMessageView ->
                        PrivateMessageReply(
                            privateMessageView = privateMessageView,
                            reply = reply,
                            onReplyChange = { reply = it },
                            onPersonClick = { personId ->
                                personClickWrapper(
                                    personProfileViewModel,
                                    personId,
                                    account,
                                    navController,
                                    ctx
                                )
                            }
                        )
                    }
                }
            }
        )
    }
}
