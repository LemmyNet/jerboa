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
import com.jerboa.feat.showBlockCommunityToast
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockCommunity
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockCommunityResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.CommunityId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BlockedCommunityViewModel(
    private val communityId: CommunityId,
) : ViewModel() {
    var blockCommunityRes: ApiState<BlockCommunityResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun blockCommunity(
        block: Boolean,
        context: Context,
    ) {
        blockCommunityRes = ApiState.Loading
        val form = BlockCommunity(communityId, block)

        viewModelScope.launch {
            blockCommunityRes = API.getInstance().blockCommunity(form).toApiState()
            withContext(Dispatchers.Main) {
                showBlockCommunityToast(blockCommunityRes, context)
            }
        }
    }

    companion object {
        class Factory(private val communityId: CommunityId) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T = BlockedCommunityViewModel(communityId) as T
        }
    }
}
