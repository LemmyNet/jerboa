package com.jerboa.ui.components.home

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneExpansionDragHandle
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import arrow.core.Either
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.SelectionVisibilityState
import com.jerboa.db.entity.AppSettings
import com.jerboa.model.AccountViewModel
import com.jerboa.model.AppSettingsViewModel
import com.jerboa.model.HomeViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.toEnum
import com.jerboa.ui.components.post.PostPane
import it.vercruysse.lemmyapi.datatypes.PostId
import kotlinx.coroutines.launch

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3AdaptiveApi::class,
    ExperimentalSharedTransitionApi::class,
    ExperimentalLayoutApi::class,
)
@Composable
fun HomeAndPostDetailScreen(
    appState: JerboaAppState,
    homeViewModel: HomeViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    appSettings: AppSettings,
    drawerState: DrawerState,
    padding: PaddingValues,
) {
    val scope = rememberCoroutineScope()

    var selectedPostId: PostId? by rememberSaveable { mutableStateOf(null) }

    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()
    val isListAndDetailVisible =
        navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Companion.Expanded &&
            navigator.scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Companion.Expanded
    val isDetailVisible =
        navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Companion.Expanded

    BackHandler(enabled = navigator.canNavigateBack()) {
        scope.launch {
            navigator.navigateBack()
        }
    }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = isListAndDetailVisible,
            label = stringResource(R.string.bottomBar_label_home),
        ) {
            ListDetailPaneScaffold(
                directive = navigator.scaffoldDirective,
                value = navigator.scaffoldValue,
                listPane = {
                    val currentSelectedId = selectedPostId
                    val selectionState =
                        if (isDetailVisible && currentSelectedId != null) {
                            SelectionVisibilityState.ShowSelection(currentSelectedId)
                        } else {
                            SelectionVisibilityState.NoSelection
                        }

                    AnimatedPane {
                        HomePane(
                            appState = appState,
                            homeViewModel = homeViewModel,
                            accountViewModel = accountViewModel,
                            siteViewModel = siteViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                            useCustomTabs = appSettings.useCustomTabs,
                            usePrivateTabs = appSettings.usePrivateTabs,
                            blurNSFW = appSettings.blurNSFW.toEnum(),
                            postActionBarMode = appSettings.postActionBarMode.toEnum(),
                            swipeToActionPreset = appSettings.swipeToActionPreset.toEnum(),
                            showPostLinkPreviews = appSettings.showPostLinkPreviews,
                            markAsReadOnScroll = appSettings.markAsReadOnScroll,
                            drawerState = drawerState,
                            padding = padding,
                            onPostClick = { postView ->
                                selectedPostId = postView.post.id
                                scope.launch {
                                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                                }
                            },
                            selectionState = selectionState,
                        )
                    }
                },
                detailPane = {
                    AnimatedPane {
                        selectedPostId?.let {
                            PostPane(
                                postOrCommentId = Either.Left(it),
                                accountViewModel = accountViewModel,
                                appState = appState,
                                showCollapsedCommentContent = appSettings.showCollapsedCommentContent,
                                showActionBarByDefault = appSettings.showCommentActionBarByDefault,
                                showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                                showParentCommentNavigationButtons = appSettings.showParentCommentNavigationButtons,
                                navigateParentCommentsWithVolumeButtons = appSettings.navigateParentCommentsWithVolumeButtons,
                                siteViewModel = siteViewModel,
                                useCustomTabs = appSettings.useCustomTabs,
                                usePrivateTabs = appSettings.usePrivateTabs,
                                blurNSFW = appSettings.blurNSFW.toEnum(),
                                showPostLinkPreview = appSettings.showPostLinkPreviews,
                                postActionBarMode = appSettings.postActionBarMode.toEnum(),
                                swipeToActionPreset = appSettings.swipeToActionPreset.toEnum(),
                            )
                        }
                    }
                },
                paneExpansionState = rememberPaneExpansionState(navigator.scaffoldValue),
                paneExpansionDragHandle = { state ->
                    PaneExpansionDragHandle(state, MaterialTheme.colorScheme.onSurfaceVariant)
                },
            )
        }
    }
}
