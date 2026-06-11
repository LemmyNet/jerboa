package com.jerboa.feat

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageProxySupportTest {
    @Test
    fun shouldIdentifyImageProxyEndpoints() {
        listOf(
            listOf(
                "https://example.com/api/v3/image_proxy",
                true,
            ),
            listOf(
                "https://example.com/api/v4/image/proxy",
                true,
            ),
            listOf(
                "https://example.com/api/v1/some_other_endpoint",
                false,
            ),
            listOf(
                "https://lemmy.ml/api/v3/image_proxy?url=https%3A%2F%2Flemmy.world%2Fpictrs%2Fimage%2Fb78339bf-95ab-4a61-b9ab-bb67696b2a4d.webp",
                true,
            ),
        ).forEach {
            val input = it[0] as String
            val expected = it[1] as Boolean
            val result = isImageProxyEndpoint(Uri.parse(input))
            Assert.assertEquals(expected, result)
        }
    }

    @Test
    fun shouldExtractProxiedImageUrl() {
        listOf(
            listOf(
                "https://lemmy.ml/api/v3/image_proxy?url=https%3A%2F%2Flemmy.world%2Fpictrs%2Fimage%2Fb78339bf-95ab-4a61-b9ab-bb67696b2a4d.webp",
                "https://lemmy.world/pictrs/image/b78339bf-95ab-4a61-b9ab-bb67696b2a4d.webp",
            ),
        ).forEach {
            val input = it[0]
            val expected = it[1]
            val result = getProxiedImageUrl(Uri.parse(input))
            Assert.assertEquals(expected, result.toString())
        }
    }
}
