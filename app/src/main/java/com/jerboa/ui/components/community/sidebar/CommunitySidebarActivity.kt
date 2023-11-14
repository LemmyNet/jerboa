package com.jerboa.ui.components.community.sidebar

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.datatypes.types.CommunityView
import com.jerboa.hostName
import com.jerboa.ui.components.common.SimpleTopAppBar

object CommunityViewSidebar {
    const val COMMUNITY_VIEW = "side-bar::return(community-view)"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitySidebarActivity(
    appState: JerboaAppState,
    onClickBack: () -> Unit,
) {
    Log.d("jerboa", "got to community sidebar activity")
    val view = appState.getPrevReturn<CommunityView>(CommunityViewSidebar.COMMUNITY_VIEW)

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                text =
                    stringResource(
                        R.string.actionbar_info_header,
                        view.community.name,
                        hostName(view.community.actor_id) ?: "invalid_actor_id",
                    ),
                onClickBack = onClickBack,
            )
        },
        content = { padding ->
            CommunitySidebar(communityView = view, padding = padding)
        },
    )
}
