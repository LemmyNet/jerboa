package com.jerboa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.jerboa.api.ApiState
import it.vercruysse.lemmyapi.v0x19.datatypes.GetPostsResponse

class PostLikesViewModel: ViewModel() {
var postLikesRes: ApiState<ListPo> by mutableStateOf(ApiState.Empty)
    private set
private var page by mutableIntStateOf(1)

}
