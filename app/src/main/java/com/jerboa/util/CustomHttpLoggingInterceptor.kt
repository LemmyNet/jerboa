package com.jerboa.util

import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.charset
import okhttp3.internal.http.promisesBody
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.internal.isProbablyUtf8
import okio.Buffer
import okio.GzipSource
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * Based of [HttpLoggingInterceptor], redacts the giving fields
 */
class CustomHttpLoggingInterceptor @JvmOverloads constructor(
    private val redactedQueryParams: Set<String> = emptySet(),
    private val redactedBodyFields: Set<String> = emptySet(),
    private val redaction: String = "REDACTED",
    private val logger: Logger = Logger.DEFAULT,
) : Interceptor {

    fun interface Logger {
        fun log(message: String)

        companion object {
            /** A [Logger] defaults output appropriate for the current platform. */
            @JvmField
            val DEFAULT: Logger = DefaultLogger()
            private class DefaultLogger : Logger {
                override fun log(message: String) {
                    Platform.get().log(message)
                }
            }
        }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val requestBody = request.body

        val connection = chain.connection()
        var requestStartMessage =
            ("--> ${request.method} ${redactQueryParams(request.url)}${if (connection != null) " " + connection.protocol() else ""}")
        if (requestBody != null) {
            requestStartMessage += " (${requestBody.contentLength()}-byte body)"
        }
        logger.log(requestStartMessage)

        val headers = request.headers

        if (requestBody != null) {
            // Request body headers are only present when installed as a network interceptor. When not
            // already present, force them to be included (if available) so their values are known.
            requestBody.contentType()?.let {
                if (headers["Content-Type"] == null) {
                    logger.log("Content-Type: $it")
                }
            }
        }

        for (i in 0 until headers.size) {
            logHeader(headers, i)
        }

        if (requestBody == null) {
            logger.log("--> END ${request.method}")
        } else if (bodyHasUnknownEncoding(request.headers)) {
            logger.log("--> END ${request.method} (encoded body omitted)")
        } else if (requestBody.isDuplex()) {
            logger.log("--> END ${request.method} (duplex request body omitted)")
        } else if (requestBody.isOneShot()) {
            logger.log("--> END ${request.method} (one-shot body omitted)")
        } else {
            var buffer = Buffer()
            requestBody.writeTo(buffer)

            var gzippedLength: Long? = null
            if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                gzippedLength = buffer.size
                GzipSource(buffer).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                }
            }

            logger.log("")
            if (!buffer.isProbablyUtf8()) {
                logger.log(
                    "--> END ${request.method} (binary ${requestBody.contentLength()}-byte body omitted)",
                )
            } else if (gzippedLength != null) {
                logger.log("--> END ${request.method} (${buffer.size}-byte, $gzippedLength-gzipped-byte body)")
            } else {
                val charset: Charset = requestBody.contentType().charset()
                logger.log(redactBody(buffer.readString(charset)))
                logger.log("--> END ${request.method} (${requestBody.contentLength()}-byte body)")
            }
        }

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            logger.log("<-- HTTP FAILED: $e")
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body
        val contentLength = responseBody.contentLength()
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        logger.log(
            "<-- ${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} ${redactQueryParams(response.request.url)} (${tookMs}ms${", $bodySize body"})",
        )

        val headersRes = response.headers
        for (i in 0 until headersRes.size) {
            logHeader(headersRes, i)
        }

        if (!response.promisesBody()) {
            logger.log("<-- END HTTP")
        } else if (bodyHasUnknownEncoding(response.headers)) {
            logger.log("<-- END HTTP (encoded body omitted)")
        } else if (bodyIsStreaming(response)) {
            logger.log("<-- END HTTP (streaming)")
        } else {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            var buffer = source.buffer

            var gzippedLength: Long? = null
            if ("gzip".equals(headersRes["Content-Encoding"], ignoreCase = true)) {
                gzippedLength = buffer.size
                GzipSource(buffer.clone()).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                }
            }

            val charset: Charset = responseBody.contentType().charset()

            if (!buffer.isProbablyUtf8()) {
                logger.log("")
                logger.log("<-- END HTTP (binary ${buffer.size}-byte body omitted)")
                return response
            }

            if (contentLength != 0L) {
                logger.log("")
                logger.log(redactBody(buffer.clone().readString(charset)))
            }

            if (gzippedLength != null) {
                logger.log("<-- END HTTP (${buffer.size}-byte, $gzippedLength-gzipped-byte body)")
            } else {
                logger.log("<-- END HTTP (${buffer.size}-byte body)")
            }
        }

        return response
    }

    private fun logHeader(headers: Headers, i: Int) {
        val value = headers.value(i)
        logger.log(headers.name(i) + ": " + value)
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
            !contentEncoding.equals("gzip", ignoreCase = true)
    }

    private fun bodyIsStreaming(response: Response): Boolean {
        val contentType = response.body.contentType()
        return contentType != null && contentType.type == "text" && contentType.subtype == "event-stream"
    }

    private fun redactQueryParams(httpUrl: HttpUrl): String {
        val builder = httpUrl.newBuilder()
        val toRedact = httpUrl.queryParameterNames.intersect(redactedQueryParams)
        toRedact.forEach { builder.setQueryParameter(it, redaction) }
        return builder.toString()
    }

    private fun redactBody(body: String): String {
        return try {
            val json = JSONObject(body)
            redactedBodyFields.filter { json.has(it) }.forEach { json.put(it, redaction) }
            json.toString()
        } catch (_: JSONException) {
            body
        }
    }
}
