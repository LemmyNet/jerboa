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
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
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
import com.jerboa.model.MyUserInfoViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.toBool
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
    myUserInfoViewModel: MyUserInfoViewModel,
    // TODO why are these duped, just use one
    appSettingsViewModel: AppSettingsViewModel,
    appSettings: AppSettings,
    drawerState: DrawerState,
    lowBandwidthMode: Boolean,
    padding: PaddingValues,
) {
    val scope = rememberCoroutineScope()

    var selectedPostId: PostId? by rememberSaveable { mutableStateOf(null) }

    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()
    val isListAndDetailVisible =
        navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded &&
            navigator.scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded
    val isDetailVisible =
        navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded

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
                            myUserInfoViewModel = myUserInfoViewModel,
                            appSettings = appSettings,
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
                                disableVideoAutoplay = appSettings.disableVideoAutoplay.toBool(),
                                lowBandwidthMode = lowBandwidthMode,
                                onClickBack = {
                                    scope.launch {
                                        selectedPostId = null
                                        navigator.navigateBack()
                                    }
                                },
                            )
                        }
                    }
                },
                paneExpansionState = rememberPaneExpansionState(navigator.scaffoldValue),
            )
        }
    }
}
