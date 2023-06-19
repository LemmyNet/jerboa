package com.jerboa.nav

import androidx.navigation.NavController
import com.jerboa.ui.components.comment.edit.CommentEditDependencies
import com.jerboa.ui.components.comment.edit.ToCommentEdit
import com.jerboa.ui.components.comment.reply.CommentReplyDependencies
import com.jerboa.ui.components.comment.reply.ToCommentReply
import com.jerboa.ui.components.community.ToCommunity
import com.jerboa.ui.components.community.list.CommunityListDependencies
import com.jerboa.ui.components.community.list.ToCommunityList
import com.jerboa.ui.components.community.sidebar.ToCommunitySideBar
import com.jerboa.ui.components.home.ToHome
import com.jerboa.ui.components.home.ToSiteSideBar
import com.jerboa.ui.components.login.ToLogin
import com.jerboa.ui.components.person.ToProfile
import com.jerboa.ui.components.post.ToComment
import com.jerboa.ui.components.post.ToPost
import com.jerboa.ui.components.post.create.CreatePostDependencies
import com.jerboa.ui.components.post.create.ToCreatePost
import com.jerboa.ui.components.post.edit.PostEditDependencies
import com.jerboa.ui.components.post.edit.ToPostEdit
import com.jerboa.ui.components.privatemessage.PrivateMessageReplyDependencies
import com.jerboa.ui.components.privatemessage.ToPrivateMessageReply
import com.jerboa.ui.components.report.ToCommentReport
import com.jerboa.ui.components.report.ToPostReport
import com.jerboa.ui.components.settings.ToAbout
import com.jerboa.ui.components.settings.ToAccountSettings
import com.jerboa.ui.components.settings.ToLookAndFeel
import com.jerboa.ui.components.settings.ToSettings

fun NavController.toLogin() = ToLogin { navigate(Route.LOGIN) }

fun NavController.toHome() = ToHome {
    navigate(Route.HOME) {
        popUpTo(0)
    }
}

fun NavController.toCommunity() = ToCommunity { id ->
    navigate(Route.CommunityFromIdArgs.makeRoute(id = "$id"))
}

fun NavController.toCommunitySideBar() = ToCommunitySideBar { navigate(Route.COMMUNITY_SIDEBAR) }

fun NavController.toProfile() = ToProfile { id, saved ->
    navigate(Route.ProfileFromIdArgs.makeRoute(id = "$id", saved = "$saved"))
}

fun NavController.toCommunityList(
    container: DependencyContainer<CommunityListDependencies>,
) = ToCommunityList(container) { navigate(Route.COMMUNITY_LIST) }

fun NavController.toCreatePost(
    container: DependencyContainer<CreatePostDependencies>,
) = ToCreatePost(container) { navigate(Route.CREATE_POST) }

fun NavController.toPost() = ToPost { id ->
    navigate(Route.PostArgs.makeRoute(id = "$id"))
}

fun NavController.toComment() = ToComment { id ->
    navigate(Route.CommentArgs.makeRoute(id = "$id"))
}

fun NavController.toCommentReply(
    container: DependencyContainer<CommentReplyDependencies>,
) = ToCommentReply(container) { navigate(Route.COMMENT_REPLY) }

fun NavController.toSiteSideBar() = ToSiteSideBar { navigate(Route.SITE_SIDEBAR) }

fun NavController.toCommentEdit(
    container: DependencyContainer<CommentEditDependencies>,
) = ToCommentEdit(container) { navigate(Route.COMMENT_EDIT) }

fun NavController.toPostEdit(
    container: DependencyContainer<PostEditDependencies>,
) = ToPostEdit(container) { navigate(Route.POST_EDIT) }

fun NavController.toPrivateMessageReply(
    container: DependencyContainer<PrivateMessageReplyDependencies>,
) = ToPrivateMessageReply(container) { navigate(Route.PRIVATE_MESSAGE_REPLY) }

fun NavController.toCommentReport() = ToCommentReport { id ->
    navigate(Route.CommentReportArgs.makeRoute(id = "$id"))
}

fun NavController.toPostReport() = ToPostReport { id ->
    navigate(Route.PostReportArgs.makeRoute(id = "$id"))
}

fun NavController.toSettings() = ToSettings { navigate(Route.SETTINGS) }

fun NavController.toAccountSettings() = ToAccountSettings { navigate(Route.ACCOUNT_SETTINGS) }

fun NavController.toLookAndFeel() = ToLookAndFeel { navigate(Route.LOOK_AND_FEEL) }

fun NavController.toAbout() = ToAbout { navigate(Route.ABOUT) }
