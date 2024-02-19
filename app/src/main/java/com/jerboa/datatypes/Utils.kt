package com.jerboa.datatypes

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jerboa.R
import it.vercruysse.lemmyapi.v0x19.datatypes.Comment
import it.vercruysse.lemmyapi.v0x19.datatypes.Person

fun Person.getDisplayName(): String = this.display_name ?: this.name

@Composable
fun Comment.getContent(): String =
    if (this.removed) {
        stringResource(R.string.comment_body_removed)
    } else if (this.deleted) {
        stringResource(R.string.comment_body_deleted)
    } else {
        this.content
    }
