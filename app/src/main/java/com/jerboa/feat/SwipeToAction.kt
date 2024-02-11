package com.jerboa.feat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.jerboa.R

enum class SwipeToActionType {
    Upvote,
    Downvote,
    Reply,
    Save,
    ;

    companion object {
        const val START_THRESHOLD = 0.05f

        fun getActionToRangeList(actions: List<SwipeToActionType>): List<Pair<OpenEndRange<Float>, SwipeToActionType>> {
            val start = START_THRESHOLD + 0.05f
            val delta = if (actions.size > 2) 0.14f else 0.2f
            return actions.mapIndexed { index, it ->
                (start + delta * index)
                    .rangeUntil(if (index == actions.size - 1) 1f else (start + delta * (index + 1))) to it
            }
        }
    }

    @Composable
    fun getImageVector(): ImageVector {
        return when (this) {
            Upvote -> ImageVector.vectorResource(id = R.drawable.up_outline)
            Downvote -> ImageVector.vectorResource(id = R.drawable.down_outline)
            Reply -> Icons.AutoMirrored.Outlined.Comment
            Save -> Icons.Outlined.Bookmark
        }
    }

    @Composable
    fun getActionColor(): ActionColor {
        return when (this) {
            Upvote -> ActionColor(
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                background = MaterialTheme.colorScheme.secondaryContainer,
            )
            Downvote -> ActionColor(
                tint = MaterialTheme.colorScheme.onErrorContainer,
                background = MaterialTheme.colorScheme.errorContainer,
            )
            Reply -> ActionColor(
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                background = MaterialTheme.colorScheme.tertiaryContainer,
            )
            Save -> ActionColor(
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                background = MaterialTheme.colorScheme.primaryContainer,
            )
        }
    }
}

data class ActionColor(
    val tint: Color,
    val background: Color,
)

enum class SwipeToActionPreset(
    val leftActions: List<SwipeToActionType>,
    val rightActions: List<SwipeToActionType>,
    val resId: Int,
) {
    DISABLED(emptyList(), emptyList(), R.string.swipe_action_preset_disabled),
    DEFAULT(
        listOf(SwipeToActionType.Reply, SwipeToActionType.Save),
        listOf(SwipeToActionType.Upvote, SwipeToActionType.Downvote),
        R.string.swipe_action_preset_default,
    ),
    LEFT_DOWNVOTE_RIGHT_UPVOTE(
        listOf(SwipeToActionType.Downvote, SwipeToActionType.Reply),
        listOf(SwipeToActionType.Upvote, SwipeToActionType.Save),
        R.string.swipe_action_preset_downvote_on_left_upvote_on_right,
    ),
    ONLY_RIGHT(
        emptyList(),
        listOf(
            SwipeToActionType.Upvote,
            SwipeToActionType.Downvote,
            SwipeToActionType.Reply,
            SwipeToActionType.Save,
        ),
        R.string.only_right_swipe_action_preset,
    ),
    ONLY_LEFT(
        listOf(
            SwipeToActionType.Upvote,
            SwipeToActionType.Downvote,
            SwipeToActionType.Reply,
            SwipeToActionType.Save,
        ),
        emptyList(),
        R.string.only_left_swipe_action_preset,
    ),
    ONLY_VOTES(
        listOf(SwipeToActionType.Downvote),
        listOf(SwipeToActionType.Upvote),
        R.string.swipe_action_preset_only_votes,
    ),
}
