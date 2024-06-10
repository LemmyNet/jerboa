package com.jerboa.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.feat.showBlockCommunityToast
import com.jerboa.feat.showBlockInstanceToast
import com.jerboa.feat.showBlockPersonToast
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockCommunity
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockCommunityResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockInstance
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockInstanceResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockPerson
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockPersonResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.CommunityId
import it.vercruysse.lemmyapi.v0x19.datatypes.Instance
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BlockedElementViewModel : ViewModel() {
    var blockCommunityRes: ApiState<BlockCommunityResponse> by mutableStateOf(ApiState.Empty)
        private set

    var blockInstanceRes: ApiState<BlockInstanceResponse> by mutableStateOf(ApiState.Empty)
        private set

    var blockPersonRes: ApiState<BlockPersonResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun blockCommunity(
        block: Boolean,
        communityId: CommunityId,
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

    fun blockInstance(
        block: Boolean,
        instance: Instance,
        context: Context,
    ) {
        blockInstanceRes = ApiState.Loading
        val form = BlockInstance(instance.id, block)

        viewModelScope.launch {
            blockInstanceRes = API.getInstance().blockInstance(form).toApiState()
            withContext(Dispatchers.Main) {
                showBlockInstanceToast(blockInstanceRes, instance, context)
            }
        }
    }

    fun blockPerson(
        block: Boolean,
        personId: PersonId,
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
}
