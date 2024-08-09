package com.jerboa.ui.components.person

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NoAccounts
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.R
import com.jerboa.datatypes.getDisplayName
import com.jerboa.datatypes.samplePerson
import com.jerboa.datatypes.samplePerson2
import com.jerboa.ui.components.common.CircularIcon
import com.jerboa.ui.components.common.ItemAndInstanceTitle
import com.jerboa.ui.components.common.TextBadge
import com.jerboa.ui.theme.SMALL_PADDING
import it.vercruysse.lemmyapi.datatypes.Person
import it.vercruysse.lemmyapi.datatypes.PersonId

@Composable
fun PersonName(
    person: Person,
    color: Color = MaterialTheme.colorScheme.tertiary,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    isPostCreator: Boolean = false,
) {
    val name = person.getDisplayName()

    if (isPostCreator) {
        TextBadge(text = name, textStyle = style)
    } else {
        ItemAndInstanceTitle(
            title = name,
            actorId = person.actor_id,
            local = person.local,
            itemColor = color,
            itemStyle = style,
            onClick = null,
        )
    }
}

@Preview
@Composable
fun PersonNamePreview() {
    PersonName(person = samplePerson, isPostCreator = false)
}

@Preview
@Composable
fun PersonNameFederatedPreview() {
    PersonName(person = samplePerson2, isPostCreator = false)
}

@Composable
fun PersonProfileLink(
    person: Person,
    onClick: (personId: PersonId) -> Unit,
    modifier: Modifier = Modifier,
    clickable: Boolean = true,
    showTags: Boolean = false,
    isPostCreator: Boolean = false,
    isDistinguished: Boolean = false,
    isCommunityBanned: Boolean = false,
    color: Color = MaterialTheme.colorScheme.tertiary,
    showAvatar: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
        modifier =
            if (clickable) {
                modifier.clickable { onClick(person.id) }
            } else {
                modifier
            },
    ) {
        if (showAvatar) {
            person.avatar?.also {
                CircularIcon(
                    icon = it,
                    contentDescription = null,
                )
            }
        }
        if (showTags) {
            if (isDistinguished) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = stringResource(R.string.person_iconModerator),
                    tint = MaterialTheme.colorScheme.tertiary,
                )
            }
            if (isCommunityBanned || person.banned) {
                Icon(
                    imageVector = Icons.Outlined.NoAccounts,
                    contentDescription = stringResource(R.string.person_iconBanned),
                    tint = Color.Red,
                )
            }
            if (person.bot_account) {
                Icon(
                    imageVector = Icons.Outlined.SmartToy,
                    contentDescription = stringResource(R.string.person_iconBot),
                    tint = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
        PersonName(
            person = person,
            isPostCreator = isPostCreator,
            color = color,
        )
    }
}

@Preview
@Composable
fun PersonProfileLinkPreview() {
    PersonProfileLink(
        person = samplePerson,
        onClick = {},
        showAvatar = true,
    )
}

@Preview
@Composable
fun PersonProfileLinkPreviewTags() {
    PersonProfileLink(
        person = samplePerson,
        isPostCreator = true,
        isCommunityBanned = true,
        isDistinguished = true,
        showTags = true,
        onClick = {},
        showAvatar = true,
    )
}
