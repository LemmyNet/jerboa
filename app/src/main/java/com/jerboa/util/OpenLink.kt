package com.jerboa.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.navigation.NavController
import com.jerboa.R
import com.jerboa.api.API

enum class BrowserType(val id: Int, val resourceId: Int) {
    External(0, R.string.browserType_external),
    CustomTab(1, R.string.browserType_customTab),
    PrivateCustomTab(2, R.string.browserType_privateCustomTab),
}

enum class LinkType {
    Community,
    User,
    Other,
}

fun looksLikeCommunityUrl(url: String): Boolean {
    Uri.parse(url).also {
        return it.pathSegments.size == 2 && it.pathSegments[0] == "c"
    }
}

fun looksLikeUserUrl(url: String): Boolean {
    Uri.parse(url).also {
        return it.pathSegments.size == 2 && it.pathSegments[0] == "u"
    }
}

/*
 * Parses a "url" and returns a spec-compliant Url, along with a LinkType:
 *
 * - https://host/path - leave as-is
 * - http://host/path - leave as-is
 * - /c/community -> https://currentInstance/c/community
 * - /c/community@instance -> https://instance/c/community
 * - !community@instance -> https://instance/c/community
 * - @user@instance -> https://instance/u/user
 */
fun parseUrl(url: String): Pair<String, LinkType>? {
    if (url.startsWith("https://") || url.startsWith("http://")) {
        if (looksLikeCommunityUrl(url)) {
            return Pair(url, LinkType.Community)
        }
        if (looksLikeUserUrl(url)) {
            return Pair(url, LinkType.User)
        }
        return Pair(url, LinkType.Other)
    } else if (url.startsWith("/c/")) {
        if (url.count { c -> c == '@' } == 1) {
            val (community, host) = url.split("@", limit = 2)
            return Pair("https://$host$community", LinkType.Community)
        }
        return Pair("https://${API.currentInstance}$url", LinkType.Community)
    } else if (url.startsWith("/u/")) {
        if (url.count { c -> c == '@' } == 1) {
            val (userPath, host) = url.split("@", limit = 2)
            return Pair("https://$host$userPath", LinkType.User)
        }
        return Pair("https://${API.currentInstance}$url", LinkType.User)
    } else if (url.startsWith("!")) {
        if (url.count { c -> c == '@' } == 1) {
            val (community, host) = url.substring(1).split("@", limit = 2)
            return Pair("https://$host/c/$community", LinkType.Community)
        }
        return Pair("https://${API.currentInstance}/c/${url.substring(1)}", LinkType.Community)
    } else if (url.startsWith("@")) {
        if (url.count { c -> c == '@' } == 2) {
            val (user, host) = url.substring(1).split("@", limit = 2)
            return Pair("https://$host/u/$user", LinkType.User)
        }
        return Pair("https://${API.currentInstance}/u/${url.substring(1)}", LinkType.User)
    }
    return null
}

/**
 * Shows a toast indicating that the URL is invalid.
 */
fun showInvalidUrlToast(context: Context, url: String) {
    Toast.makeText(context, "Invalid URL: $url", Toast.LENGTH_SHORT).show()
}

/**
 * Opens a community URL in the app. Assumes that the URL has already been validated.
 */
fun openCommunityUrl(url: String, navController: NavController) {
    Uri.parse(url).also {
        navController.navigate("${it.host}/c/${it.pathSegments[1]}")
    }
}

/**
 * Opens a user URL, either in the app. Assumes that the URL has already been validated.
 */
fun openUserUrl(url: String, navController: NavController) {
    Uri.parse(url).also {
        navController.navigate("${it.host}/u/${it.pathSegments[1]}")
    }
}

/**
 * Opens a URL in the app, or in a browser if the URL is not valid.
 */
fun openLink(url: String, navController: NavController, browserType: BrowserType) {
    val (parsedUrl, linkType) = parseUrl(url) ?: return showInvalidUrlToast(navController.context, url)

    if (linkType == LinkType.Community) {
        openCommunityUrl(parsedUrl, navController)
        return
    }
    if (linkType == LinkType.User) {
        openUserUrl(parsedUrl, navController)
        return
    }

    when (browserType) {
        BrowserType.CustomTab, BrowserType.PrivateCustomTab -> {
            val intent = CustomTabsIntent.Builder()
                .build().apply {
                    if (browserType == BrowserType.PrivateCustomTab) {
                        intent.putExtra(
                            "com.google.android.apps.chrome.EXTRA_OPEN_NEW_INCOGNITO_TAB",
                            true,
                        )
                    }
                }
            intent.launchUrl(navController.context, Uri.parse(parsedUrl))
        }

        BrowserType.External -> {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(parsedUrl))
            navController.context.startActivity(intent)
        }
    }
}
