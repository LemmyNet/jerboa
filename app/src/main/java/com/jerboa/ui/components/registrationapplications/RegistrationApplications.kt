package com.jerboa.ui.components.registrationapplications

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.UnreadOrAll
import com.jerboa.api.ApiState
import com.jerboa.datatypes.getLocalizedUnreadOrAllName
import com.jerboa.datatypes.sampleApprovedRegistrationApplicationView
import com.jerboa.datatypes.sampleDeniedRegistrationApplicationView
import com.jerboa.datatypes.samplePendingRegistrationApplicationView
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.feat.doIfReadyElseDisplayInfo
import com.jerboa.model.RegistrationApplicationsViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.DualHeaderTitle
import com.jerboa.ui.components.common.JerboaLoadingBar
import com.jerboa.ui.components.common.MarkdownTextField
import com.jerboa.ui.components.common.MyMarkdownText
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.components.common.TriggerWhenReachingEnd
import com.jerboa.ui.components.common.UnreadOrAllOptionsDropDown
import com.jerboa.ui.components.common.isRefreshing
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.person.PersonProfileLink
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import it.vercruysse.lemmyapi.datatypes.ApproveRegistrationApplication
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.RegistrationApplicationView
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationApplicationsHeader(
    openDrawer: () -> Unit,
    selectedUnreadOrAll: UnreadOrAll,
    onClickUnreadOrAll: (UnreadOrAll) -> Unit,
    unreadCount: Long? = null,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    var showUnreadOrAllOptions by remember { mutableStateOf(false) }

    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            RegistrationApplicationsHeaderTitle(
                unreadCount = unreadCount,
                selectedUnreadOrAll = selectedUnreadOrAll,
            )
        },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    Icons.Outlined.Menu,
                    contentDescription = stringResource(R.string.home_menu),
                )
            }
        },
        actions = {
            Box {
                IconButton(onClick = {
                    showUnreadOrAllOptions = !showUnreadOrAllOptions
                }) {
                    Icon(
                        Icons.Outlined.FilterList,
                        contentDescription = stringResource(R.string.inbox_filter),
                    )
                }

                UnreadOrAllOptionsDropDown(
                    expanded = showUnreadOrAllOptions,
                    selectedUnreadOrAll = selectedUnreadOrAll,
                    onDismissRequest = { showUnreadOrAllOptions = false },
                    onClickUnreadOrAll = {
                        showUnreadOrAllOptions = false
                        onClickUnreadOrAll(it)
                    },
                )
            }
        },
    )
}

@Composable
fun RegistrationApplicationsHeaderTitle(
    selectedUnreadOrAll: UnreadOrAll,
    unreadCount: Long? = null,
) {
    var title = stringResource(R.string.registrations)
    val ctx = LocalContext.current
    if (unreadCount != null && unreadCount > 0) {
        title = "$title ($unreadCount)"
    }
    DualHeaderTitle(topText = title, bottomText = getLocalizedUnreadOrAllName(ctx, selectedUnreadOrAll))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationApplications(
    appState: JerboaAppState,
    registrationApplicationsViewModel: RegistrationApplicationsViewModel,
    siteViewModel: SiteViewModel,
    ctx: Context,
    account: Account,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    padding: PaddingValues,
) {
    val listState = rememberLazyListState()

    TriggerWhenReachingEnd(listState, false) {
        account.doIfReadyElseDisplayInfo(
            appState,
            ctx,
            snackbarHostState,
            scope,
            siteViewModel,
        ) {
            registrationApplicationsViewModel.appendApplications()
        }
    }

    PullToRefreshBox(
        modifier = Modifier
            .padding(padding),
        isRefreshing = registrationApplicationsViewModel.applicationsRes.isRefreshing(),
        onRefresh = {
            account.doIfReadyElseDisplayInfo(
                appState,
                ctx,
                snackbarHostState,
                scope,
                siteViewModel,
            ) {
                registrationApplicationsViewModel.resetPage()
                registrationApplicationsViewModel.listApplications(
                    registrationApplicationsViewModel.getFormApplications(),
                    ApiState.Refreshing,
                )
                siteViewModel.fetchUnreadAppCount()
            }
        },
    ) {
        JerboaLoadingBar(registrationApplicationsViewModel.applicationsRes)

        when (val appsRes = registrationApplicationsViewModel.applicationsRes) {
            ApiState.Empty -> ApiEmptyText()
            is ApiState.Failure -> ApiErrorText(appsRes.msg)
            is ApiState.Holder -> {
                val apps = appsRes.data.registration_applications
                LazyColumn(
                    state = listState,
                    modifier =
                        Modifier
                            .simpleVerticalScrollbar(listState)
                            .fillMaxSize()
                            .imePadding(),
                ) {
                    items(
                        apps,
                        key = { app -> app.registration_application.id },
                        contentType = { "registrationApplication" },
                    ) { registrationApplicationView ->
                        RegistrationApplicationItem(
                            registrationApplicationView = registrationApplicationView,
                            onApproveClick = { form ->
                                account.doIfReadyElseDisplayInfo(
                                    appState,
                                    ctx,
                                    snackbarHostState,
                                    scope,
                                    siteViewModel,
                                ) {
                                    registrationApplicationsViewModel.approveOrDenyApplication(
                                        form,
                                    )
                                }
                            },
                            onPersonClick = { personId ->
                                appState.toProfile(id = personId)
                            },
                            showAvatar = siteViewModel.showAvatar(),
                            account = account,
                        )
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
fun RegistrationApplicationItem(
    registrationApplicationView: RegistrationApplicationView,
    onApproveClick: (ApproveRegistrationApplication) -> Unit,
    onPersonClick: (PersonId) -> Unit,
    showAvatar: Boolean,
    account: Account,
) {
    val app = registrationApplicationView.registration_application
    val accepted = registrationApplicationView.creator_local_user.accepted_application

    var showDenyReasonField by rememberSaveable { mutableStateOf(false) }
    var denyReason by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(""),
        )
    }

    Column(
        modifier =
            Modifier.padding(
                vertical = MEDIUM_PADDING,
                horizontal = MEDIUM_PADDING,
            ),
        verticalArrangement = spacedBy(MEDIUM_PADDING),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = spacedBy(MEDIUM_PADDING),
            ) {
                Text(
                    text = stringResource(R.string.applicant) + ": ",
                    style = MaterialTheme.typography.labelLarge,
                )
                PersonProfileLink(
                    person = registrationApplicationView.creator,
                    onClick = onPersonClick,
                    showAvatar = showAvatar,
                )
            }

            TimeAgo(
                published = app.published,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = spacedBy(MEDIUM_PADDING),
        ) {
            Text(
                text = stringResource(R.string.answer) + ": ",
                style = MaterialTheme.typography.labelLarge,
            )

            MyMarkdownText(
                markdown = app.answer,
                onClick = {},
            )
        }

        registrationApplicationView.admin?.let { admin ->
            if (accepted) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.approved_by) + ": ",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    PersonProfileLink(
                        person = admin,
                        onClick = onPersonClick,
                        showAvatar = showAvatar,
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.denied_by) + ": ",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    PersonProfileLink(
                        person = admin,
                        onClick = onPersonClick,
                        showAvatar = showAvatar,
                    )
                }
                app.deny_reason?.let { reason ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = spacedBy(MEDIUM_PADDING),
                    ) {
                        Text(
                            text = stringResource(R.string.deny_reason) + ": ",
                            style = MaterialTheme.typography.labelLarge,
                        )

                        MyMarkdownText(
                            markdown = reason,
                            onClick = {},
                        )
                    }
                }
            }
        }

        if (showDenyReasonField) {
            MarkdownTextField(
                text = denyReason,
                onTextChange = { denyReason = it },
                account = account,
                placeholder = stringResource(R.string.type_your_reason),
            )
        }

        Row(
            horizontalArrangement = spacedBy(MEDIUM_PADDING),
        ) {
            val showApproveButton = app.admin_id == null || !accepted
            val showDenyButton = app.admin_id == null || accepted

            if (showApproveButton) {
                OutlinedButton(
                    onClick = {
                        onApproveClick(
                            ApproveRegistrationApplication(
                                id = app.id,
                                approve = true,
                            ),
                        )
                    },
                ) {
                    Text(stringResource(R.string.approve))
                }
            }

            if (showDenyButton) {
                val errButtonColor = MaterialTheme.colorScheme.error
                OutlinedButton(
                    onClick = {
                        // If the reason isn't shown first, show it
                        if (!showDenyReasonField) {
                            showDenyReasonField = true
                        }
                        // If it is shown, then send the form
                        else {
                            showDenyReasonField = false
                            onApproveClick(
                                ApproveRegistrationApplication(
                                    id = app.id,
                                    approve = false,
                                    deny_reason = denyReason.text,
                                ),
                            )
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = errButtonColor),
                    border = BorderStroke(1.dp, errButtonColor),
                ) {
                    Text(stringResource(R.string.deny))
                }
            }
        }
    }
    HorizontalDivider(modifier = Modifier.padding(bottom = SMALL_PADDING))
}

@Preview
@Composable
fun PendingRegistrationApplicationItemPreview() {
    RegistrationApplicationItem(
        registrationApplicationView = samplePendingRegistrationApplicationView,
        onApproveClick = {},
        onPersonClick = {},
        showAvatar = false,
        account = AnonAccount,
    )
}

@Preview
@Composable
fun ApprovedRegistrationApplicationItemPreview() {
    RegistrationApplicationItem(
        registrationApplicationView = sampleApprovedRegistrationApplicationView,
        onApproveClick = {},
        onPersonClick = {},
        showAvatar = false,
        account = AnonAccount,
    )
}

@Preview
@Composable
fun DeniedRegistrationApplicationItemPreview() {
    RegistrationApplicationItem(
        registrationApplicationView = sampleDeniedRegistrationApplicationView,
        onApproveClick = {},
        onPersonClick = {},
        showAvatar = false,
        account = AnonAccount,
    )
}
