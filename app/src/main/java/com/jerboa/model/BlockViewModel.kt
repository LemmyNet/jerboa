package com.jerboa.model

import android.content.Context
import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.feat.showBlockCommunityToast
import com.jerboa.feat.showBlockInstanceToast
import com.jerboa.feat.showBlockPersonToast
import com.jerboa.feed.ApiActionController
import it.vercruysse.lemmyapi.datatypes.BlockCommunity
import it.vercruysse.lemmyapi.datatypes.BlockInstance
import it.vercruysse.lemmyapi.datatypes.BlockPerson
import it.vercruysse.lemmyapi.datatypes.Community
import it.vercruysse.lemmyapi.datatypes.Instance
import it.vercruysse.lemmyapi.datatypes.MyUserInfo
import it.vercruysse.lemmyapi.datatypes.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BlockViewModel : ViewModel() {
    private val instanceBlockController = ApiActionController<Instance> { it.id }
    private val communityBlockController = ApiActionController<Community> { it.id }
    private val personBlockController = ApiActionController<Person> { it.id }

    val instanceBlocks = derivedStateOf { instanceBlockController.feed }
    val communityBlocks = derivedStateOf { communityBlockController.feed }
    val personBlocks = derivedStateOf { personBlockController.feed }

    fun initData(userInfo: MyUserInfo) {
        Log.d("BlockViewModel", "initData")
        instanceBlockController.init(userInfo.instance_blocks.map { it.instance })
        communityBlockController.init(userInfo.community_blocks.map { it.community })
        personBlockController.init(userInfo.person_blocks.map { it.target })
    }

    fun unBlockInstance(
        instance: Instance,
        ctx: Context,
    ) {
        instanceBlockController.setLoading(instance)

        viewModelScope.launch {
            API
                .getInstance()
                .blockInstance(BlockInstance(instance.id, false))
                .onSuccess {
                    instanceBlockController.removeItem(instance)
                }.onFailure {
                    instanceBlockController.setFailed(instance, it)
                    withContext(Dispatchers.Main) {
                        showBlockInstanceToast(Result.failure(it), instance.domain, ctx)
                    }
                }
        }
    }

    fun unBlockCommunity(
        community: Community,
        ctx: Context,
    ) {
        communityBlockController.setLoading(community)

        viewModelScope.launch {
            API
                .getInstance()
                .blockCommunity(BlockCommunity(community.id, false))
                .onSuccess {
                    communityBlockController.removeItem(community)
                }.onFailure {
                    communityBlockController.setFailed(community, it)
                    withContext(Dispatchers.Main) {
                        showBlockCommunityToast(Result.failure(it), ctx)
                    }
                }
        }
    }

    fun unBlockPerson(
        person: Person,
        ctx: Context,
    ) {
        personBlockController.setLoading(person)

        viewModelScope.launch {
            API
                .getInstance()
                .blockPerson(BlockPerson(person.id, false))
                .onSuccess {
                    personBlockController.removeItem(person)
                }.onFailure {
                    personBlockController.setFailed(person, it)
                    withContext(Dispatchers.Main) {
                        showBlockPersonToast(Result.failure(it), ctx)
                    }
                }
        }
    }
}
