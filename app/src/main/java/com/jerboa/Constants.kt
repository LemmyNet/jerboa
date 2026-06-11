package com.jerboa

import kotlinx.serialization.json.Json

const val DEBOUNCE_DELAY = 1000L
const val MAX_POST_TITLE_LENGTH = 200

/**
 * Hides the downvote or percentage, if below this threshold
 */
const val SHOW_UPVOTE_PCT_THRESHOLD = 0.9F
const val VIEW_VOTES_LIMIT = 40L

val ALLOWED_SCHEMES = listOf("http", "https", "magnet")

// URLs
const val DONATE_LINK = "https://join-lemmy.org/donate"

val JSON = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}
