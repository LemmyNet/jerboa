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
import com.jerboa.feat.showBlockCommunityToast
import com.jerboa.jerboaApplication
import it.vercruysse.lemmyapi.datatypes.BlockCommunity
import it.vercruysse.lemmyapi.datatypes.BlockCommunityResponse
import it.vercruysse.lemmyapi.datatypes.CommunityId
import it.vercruysse.lemmyapi.datatypes.CommunityResponse
import it.vercruysse.lemmyapi.datatypes.FollowCommunity
import it.vercruysse.lemmyapi.datatypes.GetCommunity
import it.vercruysse.lemmyapi.datatypes.GetCommunityResponse
import it.vercruysse.lemmyapi.datatypes.GetPosts
import kotlinx.coroutines.launch

class CommunityViewModel(
    communityArg: Either<CommunityId, String>,
    accountRepository: AccountRepository,
) : PostsViewModel(accountRepository) {
    var communityRes: ApiState<GetCommunityResponse> by mutableStateOf(ApiState.Empty)
        private set

    private var followCommunityRes: ApiState<CommunityResponse> by mutableStateOf(ApiState.Empty)
    private var blockCommunityRes: ApiState<BlockCommunityResponse> by mutableStateOf(ApiState.Empty)
    private var communityId: CommunityId? by mutableStateOf(null)
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
            val res = API.getInstance().blockCommunity(form)
            blockCommunityRes = res.toApiState()
            showBlockCommunityToast(res, ctx)

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

    override fun getForm(): GetPosts =
        super.getForm().copy(
            community_id = communityId,
            community_name = communityName,
        )

    companion object {
        class Factory(
            private val id: Either<CommunityId, String>,
        ) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T = CommunityViewModel(id, extras.jerboaApplication().container.accountRepository) as T
        }
    }
}
