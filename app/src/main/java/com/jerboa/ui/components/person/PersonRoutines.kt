package com.jerboa.ui.components.person

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.blockPersonWrapper
import com.jerboa.api.markPrivateMessageAsReadWrapper
import com.jerboa.api.retrofitErrorHandler
import com.jerboa.datatypes.PersonSafe
import com.jerboa.datatypes.PrivateMessageView
import com.jerboa.datatypes.api.BlockPerson
import com.jerboa.datatypes.api.GetPrivateMessages
import com.jerboa.db.Account
import com.jerboa.serializeToMap
import com.jerboa.toastException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun findAndUpdatePrivateMessage(
    messages: MutableList<PrivateMessageView>,
    updatedMessageView: PrivateMessageView?,
) {
    updatedMessageView?.also { ucv ->
        val foundIndex = messages.indexOfFirst {
            it.private_message.id == ucv.private_message.id
        }
        if (foundIndex != -1) {
            messages[foundIndex] = ucv
        }
    }
}

fun fetchPrivateMessagesRoutine(
    messages: MutableList<PrivateMessageView>,
    loading: MutableState<Boolean>,
    page: MutableState<Int>,
    unreadOnly: MutableState<Boolean>,
    nextPage: Boolean = false,
    clear: Boolean = false,
    changeUnreadOnly: Boolean? = null,
    account: Account,
    ctx: Context,
    scope: CoroutineScope,
) {
    scope.launch {
        val api = API.getInstance()
        try {
            loading.value = true

            if (nextPage) {
                page.value++
            }

            if (clear) {
                page.value = 1
            }

            changeUnreadOnly?.also {
                unreadOnly.value = it
            }

            val form = GetPrivateMessages(
                page = page.value,
                unread_only = unreadOnly.value,
                auth = account.jwt,
            )
            Log.d(
                "jerboa",
                "Fetching unread replies: $form",
            )
            val newMessages = retrofitErrorHandler(
                api.getPrivateMessages(
                    form = form
                        .serializeToMap(),
                ),
            ).private_messages

            if (clear) {
                messages.clear()
            }
            messages.addAll(newMessages)
        } catch (e: Exception) {
            toastException(ctx = ctx, error = e)
        } finally {
            loading.value = false
        }
    }
}

fun markPrivateMessageAsReadRoutine(
    privateMessageView: MutableState<PrivateMessageView?>,
    messages: MutableList<PrivateMessageView>? = null,
    account: Account,
    ctx: Context,
    scope: CoroutineScope,
) {
    scope.launch {
        privateMessageView.value?.also { pmv ->
            val updatedPmv = markPrivateMessageAsReadWrapper(
                pmv,
                account,
                ctx,
            )?.private_message_view
            privateMessageView.value = updatedPmv
            messages?.also {
                findAndUpdatePrivateMessage(messages, updatedPmv)
            }
        }
    }
}

fun blockPersonRoutine(
    person: PersonSafe,
    block: Boolean,
    account: Account,
    ctx: Context,
    scope: CoroutineScope,
) {
    scope.launch {
        val form = BlockPerson(person.id, block, account.jwt)
        blockPersonWrapper(form, ctx)
        Toast.makeText(
            ctx,
            ctx.getString(R.string.person_routines_blocked, person.name),
            Toast.LENGTH_SHORT,
        ).show()
    }
}
