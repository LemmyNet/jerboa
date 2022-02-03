package com.jerboa.ui.components.person

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Badge
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Shield
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.jerboa.datatypes.PersonSafe
import com.jerboa.datatypes.samplePersonSafe
import com.jerboa.db.Account
import com.jerboa.personNameShown
import com.jerboa.ui.components.common.CircularIcon
import com.jerboa.ui.theme.SMALL_PADDING

@Composable
fun PersonName(
    person: PersonSafe?,
    color: Color = MaterialTheme.colors.secondary,
    isPostCreator: Boolean = false,
) {
    val name = person?.let { personNameShown(it) } ?: run { "Anonymous" }

    if (isPostCreator) {
        Badge(
            backgroundColor = MaterialTheme.colors.secondary
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.body1
            )
        }
    } else {
        Text(
            text = name,
            color = color,
        )
    }
}

@Preview
@Composable
fun PersonNamePreview() {
    PersonName(person = samplePersonSafe, isPostCreator = false)
}

@Composable
fun PersonProfileLink(
    person: PersonSafe,
    onClick: (personId: Int) -> Unit,
    showTags: Boolean = false,
    isPostCreator: Boolean = false,
    isModerator: Boolean = false,
    isCommunityBanned: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
        modifier = Modifier.clickable { onClick(person.id) },
    ) {
        person.avatar?.also {
            CircularIcon(icon = it)
        }
        if (showTags) {
            if (isModerator) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "TODO",
                    tint = MaterialTheme.colors.secondary
                )
            }
            if (person.admin) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "TODO",
                    tint = MaterialTheme.colors.primary
                )
            }
            if (isCommunityBanned || person.banned) {
                Icon(
                    imageVector = Icons.Default.NoAccounts,
                    contentDescription = "TODO",
                    tint = Color.Red
                )
            }
        }
        PersonName(
            person = person,
            isPostCreator = isPostCreator
        )
    }
}

@Preview
@Composable
fun PersonProfileLinkPreview() {
    PersonProfileLink(
        person = samplePersonSafe,
        onClick = {},
    )
}

@Preview
@Composable
fun PersonProfileLinkPreviewTags() {
    PersonProfileLink(
        person = samplePersonSafe,
        isPostCreator = true,
        isCommunityBanned = true,
        isModerator = true,
        showTags = true,
        onClick = {},
    )
}

fun personClickWrapper(
    personProfileViewModel: PersonProfileViewModel,
    personId: Int,
    account: Account?,
    navController: NavController,
    ctx: Context,
) {
    personProfileViewModel.fetchPersonDetails(
        id = personId,
        account = account,
        clear = true,
        ctx = ctx,
    )
    navController.navigate(route = "profile")
}
