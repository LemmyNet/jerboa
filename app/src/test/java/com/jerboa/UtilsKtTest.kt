package com.jerboa

import android.content.Context
import androidx.compose.ui.unit.dp
import com.jerboa.api.API
import com.jerboa.ui.theme.SMALL_PADDING
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.ocpsoft.prettytime.PrettyTime
import java.time.Duration
import java.time.Instant
import java.util.Date
import java.util.Locale

@RunWith(JUnitParamsRunner::class)
class UtilsKtTest {

    @JvmField
    @Rule
    val rule: MockitoRule = MockitoJUnit.rule()

    @Test
    fun testCalculateCommentOffset() {
        assertEquals(0.dp, calculateCommentOffset(0, 0))
        assertEquals(3.dp + SMALL_PADDING, calculateCommentOffset(2, 3))
        assertEquals(6.dp + SMALL_PADDING, calculateCommentOffset(-1, 3))
    }

    @Test
    fun testNewVote() {
        assertEquals(1, newVote(null, VoteType.Upvote))
        assertEquals(1, newVote(0, VoteType.Upvote))
        assertEquals(0, newVote(1, VoteType.Upvote))

        assertEquals(-1, newVote(null, VoteType.Downvote))
        assertEquals(-1, newVote(0, VoteType.Downvote))
        assertEquals(0, newVote(-1, VoteType.Downvote))
    }

    @Test
    fun testNewScore() {
        assertEquals(4, newScore(3, null, VoteType.Upvote))
        assertEquals(2, newScore(3, 1, VoteType.Upvote))
        assertEquals(5, newScore(3, -1, VoteType.Upvote))

        assertEquals(2, newScore(3, null, VoteType.Downvote))
        assertEquals(1, newScore(3, 1, VoteType.Downvote))
        assertEquals(4, newScore(3, -1, VoteType.Downvote))
    }

    @Test
    fun testNewVoteCount() {
        assertEquals(Pair(3, 3), newVoteCount(Pair(2, 3), null, VoteType.Upvote))
        assertEquals(Pair(3, 2), newVoteCount(Pair(2, 3), -1, VoteType.Upvote))
        assertEquals(Pair(3, 3), newVoteCount(Pair(2, 3), 0, VoteType.Upvote))
        assertEquals(Pair(1, 3), newVoteCount(Pair(2, 3), 1, VoteType.Upvote))

        assertEquals(Pair(2, 4), newVoteCount(Pair(2, 3), null, VoteType.Downvote))
        assertEquals(Pair(2, 2), newVoteCount(Pair(2, 3), -1, VoteType.Downvote))
        assertEquals(Pair(2, 4), newVoteCount(Pair(2, 3), 0, VoteType.Downvote))
        assertEquals(Pair(1, 4), newVoteCount(Pair(2, 3), 1, VoteType.Downvote))
    }

    @Test
    fun testCalculateNewInstantScores() {
        assertEquals(
            InstantScores(1, 1, 1, 0),
            calculateNewInstantScores(InstantScores(null, 0, 0, 0), VoteType.Upvote),
        )
        assertEquals(
            InstantScores(0, 0, 0, 0),
            calculateNewInstantScores(InstantScores(1, 1, 1, 0), VoteType.Upvote),
        )
    }

    @Test
    fun testHostName() {
        assertEquals("test.ml", hostName("https://test.ml/unnecessary/path"))
        assertEquals(null, hostName("invalid"))
    }

    @Test
    fun testUnreadOrAll() {
        assertEquals(UnreadOrAll.Unread, unreadOrAllFromBool(true))
        assertEquals(UnreadOrAll.All, unreadOrAllFromBool(false))
    }

    @Test
    fun testValidatePostName() {
        val ctx = mock<Context> {
            on { getString(anyInt()) } doReturn ""
        }

        assertTrue(validatePostName(ctx, "").hasError)
        assertTrue(validatePostName(ctx, "a").hasError)
        assertTrue(validatePostName(ctx, "a".repeat(MAX_POST_TITLE_LENGTH)).hasError)

        assertFalse(validatePostName(ctx, "totally fine").hasError)
        assertFalse(validatePostName(ctx, "a".repeat(MAX_POST_TITLE_LENGTH - 1)).hasError)
    }

    @Test
    fun testValidateUrl() {
        val ctx = mock<Context> {
            on { getString(R.string.url) } doReturn "url"
            on { getString(R.string.url_invalid) } doReturn "url_invalid"
        }

        assertTrue(validateUrl(ctx, "nonsense").hasError)
        assertSame("url_invalid", validateUrl(ctx, "nonsense").label)

        assertFalse(validateUrl(ctx, "").hasError)
        assertFalse(validateUrl(ctx, "https://example.com").hasError)
        assertSame("url", validateUrl(ctx, "https://example.com").label)
    }

    @Test
    fun testSerializeToMap() {
        val dataClass = InputField(label = "some label", hasError = true)
        val exp = mapOf("hasError" to dataClass.hasError.toString(), "label" to dataClass.label)
        val converted = dataClass.serializeToMap()

        exp.keys.forEach {
            assertTrue(converted.containsKey(it))
            assertEquals(exp[it], converted[it])
        }
    }

    @Test
    fun testIsImage() {
        assertTrue(isImage("http://example.com/test.jpg"))
        assertFalse(isImage("test.jpg"))
        assertFalse(isImage("http://example.com/test.csv"))
    }

    @Test
    fun testPictrsImageThumbnail() {
        assertEquals("invalid", pictrsImageThumbnail("invalid", 3))
        assertEquals(
            "http://localhost:8535/pictrs/image/file.png?thumbnail=3&format=webp",
            pictrsImageThumbnail(
                "http://localhost:8535/pictrs/image/file.png",
                3,
            ),
        )
        assertEquals(
            "http://localhost:8535/pictrs/image/file.png?thumbnail=3&format=webp",
            pictrsImageThumbnail(
                "http://localhost:8535/pictrs/image/file.png?thumbnail=256&format=jpg",
                3,
            ),
        )
    }

    @Test
    fun testGetHostFromInstanceString() {
        val cases = mapOf(
            "" to "",
            "localhost" to "localhost",
            "something useless" to "something useless",
            "https://localhost" to "localhost",
            "http://localhost" to "localhost",
            "https://localhost:443" to "localhost",
            "https://localhost:443/path" to "localhost",
            "https://localhost:443/path?param" to "localhost",
            "https://host.tld" to "host.tld",
        )
        cases.forEach { (instanceString, exp) -> assertEquals(exp, getHostFromInstanceString(instanceString)) }
    }

    @Test
    @Parameters(method = "siFormatCases")
    fun testSiFormat(expected: String, input: Int) {
        assertEquals(expected, siFormat(input))
    }

    fun siFormatCases() = listOf(
        listOf("0", 0),
        listOf("1K", 1000),
        listOf("1.1K", 1100),
        listOf("1M", 1000000),
        listOf("1.2M", 1234500),
        listOf("12M", 12345000),
    )

    @Test
    fun testParseUrl() {
        val cases = mapOf(
            "https://feddit.de" to "https://feddit.de",
            "http://example.com" to "http://example.com",
            "/c/community" to "https://${API.currentInstance}/c/community",
            "/c/community@instance.ml" to "https://instance.ml/c/community",
            "!community@instance.ml" to "https://instance.ml/c/community",
            "!community" to "https://${API.currentInstance}/c/community",
            "/u/user@instance.ml" to "https://instance.ml/u/user",
            "@user@instance.ml" to "https://instance.ml/u/user",
        )

        cases.forEach { (url, exp) -> assertEquals(exp, parseUrl(url)?.second) }
    }

    @Test
    fun testBrokenLanguagesRemappedToEnglish() {
        listOf("pl", "ru", "uk", "kk").forEach { locale ->
            val date = Date.from(Instant.now().minus(Duration.ofDays(1)))
            prettyTime = PrettyTime(Locale(locale))

            val durationString = formatDuration(date, true)
            assertNotEquals("1", durationString)
        }
    }

    @Test
    fun testEnglish() {
        val date = Date.from(Instant.now().minus(Duration.ofDays(1)))
        Locale.setDefault(Locale.ENGLISH)

        val durationString = formatDuration(date, true)
        assertEquals("1 day", durationString)
    }

    @Test
    fun compareVersions() {
        assertEquals(-1, compareVersions("0.0.1", "0.0.2"))
        assertEquals(1, compareVersions("0.0.10", "0.0.2"))
        assertEquals(1, compareVersions("0.1.10", "0.1.2"))
        assertEquals(0, compareVersions("0.1.2", "0.1.2"))
        assertEquals(-1, compareVersions("0.1.2-alpha1", "0.1.2-beta1"))
        assertEquals(1, compareVersions("0.1.2-beta1", "0.1.2-alpha2"))
    }

    @Test
    fun rewriteHttpToHttps() {
        assertEquals("https://example.com", "http://example.com".toHttps())
        assertEquals("https://example.com", "https://example.com".toHttps())
        assertEquals("example.com", "example.com".toHttps())
    }
}
