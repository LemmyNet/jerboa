package com.jerboa.ui.components.report

import androidx.navigation.NavController
import com.jerboa.nav.NavControllerWrapper

class ToPostReport(
    val navigate: (postId: Int) -> Unit,
)

class ToCommentReport(
    val navigate: (commentId: Int) -> Unit,
)

class CreateReportNavController(
    override val navController: NavController,
) : NavControllerWrapper()
