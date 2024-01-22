package com.jerboa.feat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.jerboa.R

enum class SwipeToActionType() {
    Upvote,
    Downvote,
    Reply,
    Save,
    ;

    companion object {
        fun getActionToRangeList(actions: List<SwipeToActionType>): List<Pair<OpenEndRange<Float>, SwipeToActionType>> {
            val delta = if(actions.size > 2) 0.14f else 0.18f
            return actions.mapIndexed { index, it ->
                (0.1f + delta * index)
                    .rangeUntil(if (index == actions.size - 1) 1f else (0.1f + delta * (index + 1))) to it
            }
        }

        fun getDefaultLeftActions(): List<SwipeToActionType> {
            return listOf(Reply, Save)
        }

        fun getDefaultRightActions(): List<SwipeToActionType> {
            return listOf(Upvote, Downvote)
        }
    }

    @Composable
    fun getImageVector(): ImageVector {
        return when (this) {
            Upvote -> ImageVector.vectorResource(id = R.drawable.up_outline)
            Downvote -> ImageVector.vectorResource(id = R.drawable.down_outline)
            Reply -> Icons.Outlined.Comment
            Save -> Icons.Outlined.Bookmark
        }
    }

    @Composable
    fun getActionColor(): Color {
        return when (this) {
            Upvote -> MaterialTheme.colorScheme.primary
            Downvote -> MaterialTheme.colorScheme.tertiary
            Reply -> MaterialTheme.colorScheme.onPrimary
            Save -> MaterialTheme.colorScheme.onTertiary
        }
    }
}

enum class SwipeToActionPreset(
    val leftActions: List<SwipeToActionType>,
    val rightActions: List<SwipeToActionType>,
    val resId: Int
) {
    DISABLED(emptyList(), emptyList(), R.string.disabled_swipe_action_preset),
    DEFAULT(
        listOf(SwipeToActionType.Reply, SwipeToActionType.Save),
        listOf(SwipeToActionType.Upvote, SwipeToActionType.Downvote),
        R.string.default_swipe_action_preset
    ),
    LEFT_DOWNVOTE_RIGHT_UPVOTE(
        listOf(SwipeToActionType.Downvote, SwipeToActionType.Reply),
        listOf(SwipeToActionType.Upvote, SwipeToActionType.Save),
        R.string.downvote_on_left_upvote_on_right_swipe_action_preset
    ),
    ONLY_RIGHT(
        emptyList(),
        listOf(
            SwipeToActionType.Upvote,
            SwipeToActionType.Downvote,
            SwipeToActionType.Reply,
            SwipeToActionType.Save
        ),
       R.string.only_right_swipe_action_preset
    ),
    ONLY_LEFT(
        listOf(
            SwipeToActionType.Upvote,
            SwipeToActionType.Downvote,
            SwipeToActionType.Reply,
            SwipeToActionType.Save
        ),
        emptyList(),
        R.string.only_left_swipe_action_preset
    )
}