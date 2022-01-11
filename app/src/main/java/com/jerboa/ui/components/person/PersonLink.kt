package com.jerboa.ui.components.person

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.datatypes.PersonSafe
import com.jerboa.datatypes.samplePersonSafe
import com.jerboa.ui.components.common.CircularIcon
import com.jerboa.ui.theme.SMALL_PADDING

@Composable
fun PersonName(
    person: PersonSafe,
    color: Color = MaterialTheme.colors.secondary,
) {
    val displayName =
        person.display_name ?: person.name
    Text(
        text = displayName,
        color = color,
    )
}

@Preview
@Composable
fun PersonNamePreview() {
    PersonName(person = samplePersonSafe)
}

@Composable
fun PersonLink(person: PersonSafe) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING)
    ) {
        person.avatar?.also {
            CircularIcon(icon = it)
        }
        PersonName(person = person)
    }
}

@Preview
@Composable
fun PersonLinkPreview() {
    PersonLink(person = samplePersonSafe)
}
