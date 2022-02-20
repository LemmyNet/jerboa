package com.jerboa.ui.components.private_message

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.jerboa.datatypes.api.CreatePrivateMessage
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.inbox.InboxViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.person.personClickWrapper

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

    var reply by remember { mutableStateOf(TextFieldValue("")) }

    val focusManager = LocalFocusManager.current

    Surface(color = MaterialTheme.colors.background) {
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
                            account = account,
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
