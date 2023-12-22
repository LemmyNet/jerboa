package com.jerboa.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import arrow.core.Either
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.db.repository.AccountRepository
import com.jerboa.jerboaApplication
import com.jerboa.showBlockCommunityToast
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockCommunity
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockCommunityResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.CommunityId
import it.vercruysse.lemmyapi.v0x19.datatypes.CommunityResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.FollowCommunity
import it.vercruysse.lemmyapi.v0x19.datatypes.GetCommunity
import it.vercruysse.lemmyapi.v0x19.datatypes.GetCommunityResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.GetPosts
import kotlinx.coroutines.launch

class CommunityViewModel(
    communityArg: Either<CommunityId, String>,
    accountRepository: AccountRepository,
) : PostsViewModel(accountRepository) {
    var communityRes: ApiState<GetCommunityResponse> by mutableStateOf(ApiState.Empty)
        private set

    private var followCommunityRes: ApiState<CommunityResponse> by mutableStateOf(ApiState.Empty)
    private var blockCommunityRes: ApiState<BlockCommunityResponse> by mutableStateOf(ApiState.Empty)
    private var communityId: Int? by mutableStateOf(null)
    private var communityName: String? by mutableStateOf(null)

    private fun getCommunity(form: GetCommunity) {
        viewModelScope.launch {
            communityRes = ApiState.Loading
            communityRes = API.getInstance().getCommunity(form).toApiState()
        }
    }

    fun followCommunity(
        form: FollowCommunity,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            followCommunityRes = ApiState.Loading
            followCommunityRes = API.getInstance().followCommunity(form).toApiState()

            // Copy that response to the communityRes
            when (val followRes = followCommunityRes) {
                is ApiState.Success -> {
                    val cv = followRes.data.community_view
                    when (val cRes = communityRes) {
                        is ApiState.Success -> {
                            val newCRes = cRes.data.copy(community_view = cv)
                            communityRes = ApiState.Success(newCRes)
                            onSuccess()
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    fun blockCommunity(
        form: BlockCommunity,
        ctx: Context,
    ) {
        viewModelScope.launch {
            blockCommunityRes = ApiState.Loading
            blockCommunityRes = API.getInstance().blockCommunity(form).toApiState()

            showBlockCommunityToast(blockCommunityRes, ctx)

            when (val blockCommunity = blockCommunityRes) {
                is ApiState.Success -> {
                    when (val existing = communityRes) {
                        is ApiState.Success -> {
                            val newRes =
                                ApiState.Success(
                                    existing.data.copy(
                                        community_view =
                                            blockCommunity.data.community_view,
                                    ),
                                )
                            communityRes = newRes
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    init {
        communityId = communityArg.fold({ it }, { null })
        communityName = communityArg.fold({ null }, { it })

        this.getCommunity(
            form =
                GetCommunity(
                    id = communityId,
                    name = communityName,
                ),
        )
        init()
    }

    override fun getForm(): GetPosts {
        return GetPosts(
            community_id = communityId,
            community_name = communityName,
            page_cursor = pageCursor,
            sort = sortType,
        )
    }

    companion object {
        class Factory(
            private val id: Either<CommunityId, String>,
        ) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                return CommunityViewModel(id, extras.jerboaApplication().container.accountRepository) as T
            }
        }
    }
}
