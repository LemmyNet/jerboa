package com.jerboa.state

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

@Stable
class VideoAppState {

    var exoPlayer: ExoPlayer? = null

    val activeEmbedId = mutableStateOf<Long?>(null)

    // Map to store the distance from center for each video URL
    private val videoDistances = mutableMapOf<Long, Float>()

    // Whether the video is muted
    val isVideoMuted = mutableStateOf(true)

    /**
     * Initializes the ExoPlayer instance if it doesn't exist
     * @param context The context to use for creating the ExoPlayer
     * @return The ExoPlayer instance
     */
    @Synchronized
    fun getOrCreateExoPlayer(context: Context): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
//                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_ONE
                volume = if (isVideoMuted.value) 0f else 1f
            }
        }
        return exoPlayer!!
    }

    /**
     * Releases the ExoPlayer instance
     */
    fun releaseExoPlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    /**
     * Toggles the mute state of the video
     */
    fun toggleVideoMute() {
        isVideoMuted.value = !isVideoMuted.value
        exoPlayer?.volume = if (isVideoMuted.value) 0f else 1f
    }

    /**
     * Updates the distance from center for a video URL
     * @param url The URL of the video
     * @param distance The distance from the center of the screen
     * @param isVisible Whether the video is visible
     */
    fun updateVideoDistance(id: Long, distance: Float, isVisible: Boolean) {
        if (isVisible) {
            videoDistances[id] = distance

            // Find the video with the smallest distance
            val closestVideo = videoDistances.minByOrNull { it.value }

            // Set it as the active video
            if (closestVideo != null) {
                activeEmbedId.value = closestVideo.key
            }
        } else {
            // If the video is not visible, remove it from the map
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