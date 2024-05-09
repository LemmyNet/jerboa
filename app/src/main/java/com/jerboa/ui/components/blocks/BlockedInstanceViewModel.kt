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
import com.jerboa.feat.showBlockInstanceToast
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockInstance
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockInstanceResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.Instance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BlockedInstanceViewModel(
    private val blockedInstance: Instance,
) : ViewModel() {
    var blockInstanceRes: ApiState<BlockInstanceResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun blockInstance(
        block: Boolean,
        context: Context,
    ) {
        blockInstanceRes = ApiState.Loading
        val form = BlockInstance(blockedInstance.id, block)

        viewModelScope.launch {
            blockInstanceRes = API.getInstance().blockInstance(form).toApiState()
            withContext(Dispatchers.Main) {
                showBlockInstanceToast(blockInstanceRes, blockedInstance, context)
            }
        }
    }

    companion object {
        class Factory(private val instance: Instance) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T = BlockedInstanceViewModel(instance) as T
        }
    }
}
