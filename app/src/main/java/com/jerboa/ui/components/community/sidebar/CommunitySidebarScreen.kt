package com.jerboa.ui.components.community.sidebar

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.hostName
import com.jerboa.ui.components.common.SimpleTopAppBar
import it.vercruysse.lemmyapi.datatypes.GetCommunityResponse

object CommunityViewSidebar {
    const val COMMUNITY_RES = "side-bar::return(community-res)"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitySidebarScreen(
    appState: JerboaAppState,
    onClickBack: () -> Unit,
    showAvatar: Boolean,
) {
    Log.d("jerboa", "got to community sidebar screen")
    val communityRes = appState.getPrevReturn<GetCommunityResponse>(CommunityViewSidebar.COMMUNITY_RES)
    val community = communityRes.community_view.community

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                text =
                    stringResource(
                        R.string.actionbar_info_header,
                        community.name,
                        hostName(community.actor_id) ?: "invalid_actor_id",
                    ),
                onClickBack = onClickBack,
            )
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                CommunitySidebar(
                    communityRes = communityRes,
                    onPersonClick = appState::toProfile,
                    showAvatar = showAvatar,
                )
            }
        },
    )
}
