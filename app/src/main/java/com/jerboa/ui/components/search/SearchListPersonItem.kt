package com.jerboa.ui.components.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.jerboa.personNameShown
import com.jerboa.ui.components.common.CircularIcon
import com.jerboa.ui.components.person.CommentsAndPosts
import com.jerboa.ui.theme.DRAWER_ITEM_SPACING
import com.jerboa.ui.theme.LARGER_ICON_THUMBNAIL_SIZE
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.LINK_ICON_SIZE
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonView

@Composable
fun SearchListPersonItem(
    personView: PersonView,
    onClickPerson: (PersonView) -> Unit,
    spacing: Dp = DRAWER_ITEM_SPACING,
    size: Dp = LINK_ICON_SIZE,
    thumbnailSize: Int = LARGER_ICON_THUMBNAIL_SIZE,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        modifier =
            Modifier
                .clickable { onClickPerson(personView) }
                .padding(LARGE_PADDING)
                .fillMaxWidth(),
    ) {
        val avatar = personView.person.avatar

        if (avatar != null) {
            CircularIcon(
                icon = avatar,
                contentDescription = null,
                size = size,
                thumbnailSize = thumbnailSize,
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "",
                modifier = Modifier.size(size),
            )
        }

        Column {
            PersonName(personView)
            CommentsAndPosts(personView)
        }
    }
}

@Composable
fun PersonName(
    person: PersonView,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    overflow: TextOverflow = TextOverflow.Ellipsis,
) {
    Text(
        text = personNameShown(person.person, true),
        style = style,
        color = color,
        modifier = modifier,
        overflow = overflow,
        maxLines = 1,
    )
}
