package com.jerboa.ui.components.privatemessage

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.db.entity.isAnon
import com.jerboa.model.AccountViewModel
import com.jerboa.ui.components.common.ActionTopBar
import com.jerboa.ui.components.common.MarkdownTextField
import com.jerboa.ui.components.common.getCurrentAccount
import it.vercruysse.lemmyapi.datatypes.CreatePrivateMessage
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PrivateMessageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CreatePrivateMessageScreen(
    personId: PersonId,
    personName: String,
    accountViewModel: AccountViewModel,
    onBack: () -> Unit,
) {
    Log.d("jerboa", "got to create private message screen")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)
    val scope = rememberCoroutineScope()

    var textBody by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

    var loading by remember {
        mutableStateOf(false)
    }

    val focusManager = LocalFocusManager.current
    Scaffold(
        topBar = {
            ActionTopBar(
                title = personName,
                loading = loading,
                onBackClick = onBack,
                onActionClick = {
                    if (!account.isAnon()) {
                        scope.launch {
                            loading = true

                            var res: ApiState<PrivateMessageResponse> = ApiState.Empty

                            while (res !is ApiState.Success) {
                                res =
                                    withContext(Dispatchers.IO) {
                                        API
                                            .getInstance()
                                            .createPrivateMessage(
                                                CreatePrivateMessage(
                                                    textBody.text,
                                                    personId,
                                                ),
                                            ).toApiState()
                                    }
                                if (res is ApiState.Failure) {
                                    Toast.makeText(ctx, R.string.private_message_failed, Toast.LENGTH_SHORT).show()
                                }
                            }

                            loading = false
                            focusManager.clearFocus()
                            Toast.makeText(ctx, R.string.private_message_success, Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    }
                },
                actionText = R.string.form_submit,
                actionIcon = Icons.AutoMirrored.Outlined.Send,
            )
        },
        content = { padding ->
            val scrollState = rememberScrollState()

            Column(
                modifier =
                    Modifier
                        .verticalScroll(scrollState)
                        .padding(padding)
                        .consumeWindowInsets(padding)
                        .imePadding(),
            ) {
                MarkdownTextField(
                    text = textBody,
                    onTextChange = { textBody = it },
                    account = account,
                    placeholder = stringResource(R.string.private_message_placeholder),
                )
            }
        },
    )
}
