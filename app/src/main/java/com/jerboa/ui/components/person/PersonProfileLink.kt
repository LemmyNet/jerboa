@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.person

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.datatypes.PersonSafe
import com.jerboa.datatypes.samplePersonSafe
import com.jerboa.personNameShown
import com.jerboa.ui.components.common.CircularIcon
import com.jerboa.ui.theme.SMALL_PADDING

@Composable
fun PersonName(
    person: PersonSafe?,
    color: Color = MaterialTheme.colorScheme.tertiary,
    isPostCreator: Boolean = false
) {
    val name = person?.let { personNameShown(it) } ?: run { "Anonymous" }
    val style = MaterialTheme.typography.bodyMedium

    if (isPostCreator) {
        Badge(
            containerColor = MaterialTheme.colorScheme.tertiary
        ) {
            Text(
                text = name,
                style = style
            )
        }
    } else {
        Text(
            text = name,
            color = color,
            style = style
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
    isCommunityBanned: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
        modifier = Modifier.clickable { onClick(person.id) }
    ) {
        person.avatar?.also {
            CircularIcon(icon = it)
        }
        if (showTags) {
            if (isModerator) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = "TODO",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            if (person.admin) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = "TODO",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            if (isCommunityBanned || person.banned) {
                Icon(
                    imageVector = Icons.Outlined.NoAccounts,
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
        onClick = {}
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
        onClick = {}
    )
}
