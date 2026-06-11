package com.jerboa.feat

import android.net.Uri

const val V3_IMAGE_PROXY_PATH = "api/v3/image_proxy"
const val V4_IMAGE_PROXY_PATH = "api/v4/image/proxy"

fun isImageProxyEndpoint(uri: Uri): Boolean {
    val path = uri.path ?: return false
    return path.endsWith(V3_IMAGE_PROXY_PATH) || path.endsWith(V4_IMAGE_PROXY_PATH)
}

fun getProxiedImageUrl(uri: Uri): Uri {
    val query = uri.getQueryParameter("url") ?: return uri
    return Uri.parse(query)
}

fun String.parseUriWithProxyImageSupport(): Uri {
    val parsedUri = Uri.parse(this)
    return if (isImageProxyEndpoint(parsedUri)) {
        getProxiedImageUrl(parsedUri)
    } else {
        parsedUri
    }
}
