package com.jerboa.ui.components.person

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
) {
    val name = person?.let { personNameShown(it) } ?: run { "Anonymous" }
    Text(
        text = name,
        color = color,
    )
}

@Preview
@Composable
fun PersonNamePreview() {
    PersonName(person = samplePersonSafe)
}

@Composable
fun PersonProfileLink(
    person: PersonSafe,
    onClick: (personId: Int) -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
        modifier = Modifier.clickable { onClick(person.id) },
    ) {
        person.avatar?.also {
            CircularIcon(icon = it)
        }
        PersonName(person = person)
    }
}

@Preview
@Composable
fun PersonProfileLinkPreview() {
    PersonProfileLink(person = samplePersonSafe)
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
