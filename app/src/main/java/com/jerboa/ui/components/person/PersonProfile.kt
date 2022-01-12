package com.jerboa.ui.components.person

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.MyMarkdownText
import com.jerboa.datatypes.PersonViewSafe
import com.jerboa.datatypes.samplePersonView
import com.jerboa.personNameShown
import com.jerboa.ui.components.common.LargerCircularIcon
import com.jerboa.ui.components.common.PictrsBannerImage
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.Muted
import com.jerboa.ui.theme.PROFILE_BANNER_SIZE
import com.jerboa.ui.theme.SMALL_PADDING

@Composable
fun PersonProfileTopSection(
    personView: PersonViewSafe,
    modifier: Modifier = Modifier,
) {

    Column {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomStart
        ) {
            personView.person.banner?.also {
                PictrsBannerImage(
                    url = it, modifier = Modifier.height(PROFILE_BANNER_SIZE)
                )
            }
            Box(modifier = Modifier.padding(MEDIUM_PADDING)) {
                personView.person.avatar?.also {
                    LargerCircularIcon(icon = it)
                }
            }
        }
        Card(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .padding(vertical = SMALL_PADDING)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(MEDIUM_PADDING),
                verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING)
            ) {
                Text(
                    text = personNameShown(personView.person),
                    style = MaterialTheme.typography.h6
                )

                TimeAgo(
                    precedingString = "Created",
                    includeAgo = true,
                    dateStr = personView.person.published
                )
                personView.person.bio?.also {
                    MyMarkdownText(
                        markdown = it,
                        color = Muted,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PersonProfileTopSectionPreview() {
    PersonProfileTopSection(personView = samplePersonView)
}
