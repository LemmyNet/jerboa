package com.jerboa.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jerboa.R
import com.jerboa.communityNameShown
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.personNameShown
import it.vercruysse.lemmyapi.datatypes.Person

@Composable
fun BanPersonPopupMenuItem(
    person: Person,
    onDismissRequest: () -> Unit,
    onBanPersonClick: (person: Person) -> Unit,
) {
    val personNameShown = personNameShown(person, true)
    val (banText, banIcon) =
        if (person.banned) {
            Pair(stringResource(R.string.unban_person, personNameShown), Icons.Outlined.Restore)
        } else {
            Pair(stringResource(R.string.ban_person, personNameShown), Icons.Outlined.Gavel)
        }

    PopupMenuItem(
        text = banText,
        icon = banIcon,
        onClick = {
            onDismissRequest()
            onBanPersonClick(person)
        },
    )
}

@Composable
fun BanFromCommunityPopupMenuItem(
    banData: BanFromCommunityData,
    onDismissRequest: () -> Unit,
    onBanFromCommunityClick: (banData: BanFromCommunityData) -> Unit,
) {
    val personNameShown = personNameShown(banData.person, true)
    val communityNameShown = communityNameShown(banData.community)
    val (banText, banIcon) =
        if (banData.banned) {
            Pair(stringResource(R.string.unban_person_from_community, personNameShown, communityNameShown), Icons.Outlined.Restore)
        } else {
            Pair(stringResource(R.string.ban_person_from_community, personNameShown, communityNameShown), Icons.Outlined.Gavel)
        }

    PopupMenuItem(
        text = banText,
        icon = banIcon,
        onClick = {
            onDismissRequest()
            onBanFromCommunityClick(banData)
        },
    )
}
