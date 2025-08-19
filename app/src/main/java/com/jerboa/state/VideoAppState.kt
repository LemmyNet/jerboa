package com.jerboa.state

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Stable
class VideoAppState {
    private var exoPlayer: ExoPlayer? = null

    val activeEmbedId = mutableStateOf<Long?>(null)

    // Map to store the distance from top for each video
    private val videoDistances = mutableMapOf<Long, Float>()

    val isVideoPlayerMuted = mutableStateOf(true)
    val isEmbedVideoMuted = mutableStateOf(true)

    @Synchronized
    fun getOrCreateExoPlayer(context: Context): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
                volume = if (isEmbedVideoMuted.value) 0f else 1f
            }
        }
        return exoPlayer!!
    }

    fun releaseExoPlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    fun toggleVideoMute() {
        isEmbedVideoMuted.value = !isEmbedVideoMuted.value
        exoPlayer?.volume = if (isEmbedVideoMuted.value) 0f else 1f
    }

    fun updateVideoDistance(
        id: Long,
        distance: Float,
        isVisible: Boolean,
    ) {
        if (isVisible) {
            videoDistances[id] = distance

            val closestVideo = videoDistances.minByOrNull { it.value }

            if (closestVideo != null) {
                activeEmbedId.value = closestVideo.key
            }
        } else {
            videoDistances.remove(id)

            // If the active video is no longer visible, find a new active video
            if (activeEmbedId.value == id) {
                val closestVideo = videoDistances.minByOrNull { it.value }
                activeEmbedId.value = closestVideo?.key

                if (closestVideo == null) {
                    exoPlayer?.stop()
                }
            }
        }
    }

    fun removeVideoDistance(id: Long) {
        videoDistances.remove(id)
    }
}
