package com.jerboa.ui.components.community.sidebar

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.datatypes.types.CommunityView
import com.jerboa.getPrevReturn
import com.jerboa.hostName
import com.jerboa.ui.components.common.LoadingBar
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
    val view = remember {
        mutableStateOf<CommunityView?>(null)
    }

    appState.getPrevReturn<CommunityView>(CommunityViewSidebar.COMMUNITY_VIEW) {
        view.value = it
    }

    when (val currView = view.value) {
        null -> LoadingBar()
        else -> {
            Scaffold(
                topBar = {
                    SimpleTopAppBar(
                        text = stringResource(
                            R.string.actionbar_info_header,
                            currView.community.name,
                            hostName(currView.community.actor_id) ?: "invalid_actor_id",
                        ),
                        onClickBack = onClickBack,
                    )
                },
                content = { padding ->
                    CommunitySidebar(communityView = currView, padding = padding)
                },
            )
        }
    }
}
