package com.jerboa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import it.vercruysse.lemmyapi.v0x19.datatypes.CreatePrivateMessage
import it.vercruysse.lemmyapi.v0x19.datatypes.PrivateMessageResponse
import kotlinx.coroutines.launch

class PrivateMessageReplyViewModel : ViewModel() {
    var createMessageRes: ApiState<PrivateMessageResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun createPrivateMessage(
        recipientId: Int,
        content: String,
        onGoBack: () -> Unit,
        focusManager: FocusManager,
    ) {
        viewModelScope.launch {
            val form =
                CreatePrivateMessage(
                    content = content,
                    recipient_id = recipientId,
                )
            createMessageRes = ApiState.Loading
            createMessageRes = API.getInstance().createPrivateMessage(form).toApiState()

            focusManager.clearFocus()
            onGoBack()
        }
    }
}
