package com.jerboa.ui.components.community

import androidx.navigation.NavController
import com.jerboa.nav.NavControllerWrapper
import com.jerboa.ui.components.community.sidebar.ToCommunitySideBar
import com.jerboa.ui.components.person.ToProfile
import com.jerboa.ui.components.post.ToPost
import com.jerboa.ui.components.post.create.ToCreatePost
import com.jerboa.ui.components.post.edit.ToPostEdit
import com.jerboa.ui.components.report.ToPostReport

class ToCommunity(
    val navigate: (communityId: Int) -> Unit,
)

class CommunityNavController(
    override val navController: NavController,
    val toPostEdit: ToPostEdit,
    val toCreatePost: ToCreatePost,
    val toCommunitySideBar: ToCommunitySideBar,
    val toPost: ToPost,
    val toPostReport: ToPostReport,
    val toCommunity: ToCommunity,
    val toProfile: ToProfile,
) : NavControllerWrapper()
