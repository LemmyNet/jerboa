package com.jerboa.ui.components.blocks

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.feat.showBlockPersonToast
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockPerson
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockPersonResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BlockedPersonViewModel(
    private val personId: PersonId,
) : ViewModel() {
    var blockPersonRes: ApiState<BlockPersonResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun blockPerson(
        block: Boolean,
        context: Context,
    ) {
        blockPersonRes = ApiState.Loading
        val form = BlockPerson(personId, block)

        viewModelScope.launch {
            blockPersonRes = API.getInstance().blockPerson(form).toApiState()
            withContext(Dispatchers.Main) {
                showBlockPersonToast(blockPersonRes, context)
            }
        }
    }

    companion object {
        class Factory(private val personId: PersonId) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T = BlockedPersonViewModel(personId) as T
        }
    }
}
