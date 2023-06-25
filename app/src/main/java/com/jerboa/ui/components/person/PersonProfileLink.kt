package com.jerboa.ui.components.person

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.R
import com.jerboa.datatypes.samplePerson
import com.jerboa.datatypes.types.Person
import com.jerboa.personNameShown
import com.jerboa.ui.components.common.CircularIcon
import com.jerboa.ui.components.common.TextBadge
import com.jerboa.ui.theme.SMALL_PADDING

@Composable
fun PersonName(
    person: Person?,
    color: Color = MaterialTheme.colorScheme.tertiary,
    isPostCreator: Boolean = false,
) {
    val name = person?.let { personNameShown(it) } ?: run { "Anonymous" }
    val style = MaterialTheme.typography.bodyMedium

    if (isPostCreator) {
        TextBadge(text = name, textStyle = style)
    } else {
        Text(
            text = name,
            color = color,
            style = style,
            overflow = TextOverflow.Clip,
            maxLines = 1,
        )
    }
}

@Preview
@Composable
fun PersonNamePreview() {
    PersonName(person = samplePerson, isPostCreator = false)
}

@Composable
fun PersonProfileLink(
    person: Person,
    onClick: (personId: Int) -> Unit,
    clickable: Boolean = true,
    showTags: Boolean = false,
    isPostCreator: Boolean = false,
    isModerator: Boolean = false,
    isCommunityBanned: Boolean = false,
    color: Color = MaterialTheme.colorScheme.tertiary,
    showAvatar: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
        modifier = if (clickable) {
            Modifier.clickable { onClick(person.id) }
        } else {
            Modifier
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
            if (isModerator) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = stringResource(R.string.person_iconModerator),
                    tint = MaterialTheme.colorScheme.tertiary,
                )
            }
            if (person.admin) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = stringResource(R.string.person_iconAdmin),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            if (isCommunityBanned || person.banned) {
                Icon(
                    imageVector = Icons.Outlined.NoAccounts,
                    contentDescription = stringResource(R.string.person_iconBanned),
                    tint = Color.Red,
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
        isModerator = true,
        showTags = true,
        onClick = {},
        showAvatar = true,
    )
}
