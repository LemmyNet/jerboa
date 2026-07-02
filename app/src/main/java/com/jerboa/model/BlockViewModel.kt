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
import it.vercruysse.lemmyapi.datatypes.BlockPerson
import it.vercruysse.lemmyapi.datatypes.Community
import it.vercruysse.lemmyapi.datatypes.Instance
import it.vercruysse.lemmyapi.datatypes.MyUserInfo
import it.vercruysse.lemmyapi.datatypes.Person
import it.vercruysse.lemmyapi.datatypes.UserBlockInstanceCommunitiesParams
import it.vercruysse.lemmyapi.datatypes.UserBlockInstancePersonsParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BlockViewModel : ViewModel() {
    private val instancePersonsBlockController = ApiActionController<Instance> { it.id }
    private val instanceCommunitiesBlockController = ApiActionController<Instance> { it.id }
    private val communityBlockController = ApiActionController<Community> { it.id }
    private val personBlockController = ApiActionController<Person> { it.id }

    val instancePersonsBlocks = derivedStateOf { instancePersonsBlockController.feed }
    val instanceCommunitiesBlocks = derivedStateOf { instanceCommunitiesBlockController.feed }
    val communityBlocks = derivedStateOf { communityBlockController.feed }
    val personBlocks = derivedStateOf { personBlockController.feed }

    fun initData(userInfo: MyUserInfo) {
        Log.d("BlockViewModel", "initData")
        instancePersonsBlockController.init(userInfo.instance_persons_blocks.map { it })
        instanceCommunitiesBlockController.init(userInfo.instance_communities_blocks.map { it })
        communityBlockController.init(userInfo.community_blocks.map { it })
        personBlockController.init(userInfo.person_blocks.map { it })
    }

    fun unBlockInstancePersons(
        instance: Instance,
        ctx: Context,
    ) {
        instancePersonsBlockController.setLoading(instance)

        viewModelScope.launch {
            API
                .getInstance()
                .userBlockInstancePersons(UserBlockInstancePersonsParams(instance.id, false))
                .onSuccess {
                    instancePersonsBlockController.removeItem(instance)
                }.onFailure {
                    instancePersonsBlockController.setFailed(instance, it)
                    withContext(Dispatchers.Main) {
                        showBlockInstanceToast(
                            blockInstanceResp = Result.failure(it),
                            instance = instance.domain,
                            blocked = false,
                            ctx = ctx
                        )
                    }
                }
        }
    }

    fun unBlockInstanceCommunities(
        instance: Instance,
        ctx: Context,
    ) {
        instanceCommunitiesBlockController.setLoading(instance)

        viewModelScope.launch {
            API
                .getInstance()
                .userBlockInstanceCommunities(UserBlockInstanceCommunitiesParams(instance.id, false))
                .onSuccess {
                    instanceCommunitiesBlockController.removeItem(instance)
                }.onFailure {
                    instanceCommunitiesBlockController.setFailed(instance, it)
                    withContext(Dispatchers.Main) {
                        showBlockInstanceToast(
                            blockInstanceResp = Result.failure(it),
                            instance = instance.domain,
                            blocked = false,
                            ctx = ctx
                        )
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
