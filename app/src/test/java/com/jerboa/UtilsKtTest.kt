package com.jerboa

import android.content.Context
import androidx.compose.ui.unit.dp
import com.jerboa.datatypes.sampleCommentView
import com.jerboa.feat.InstantScores
import com.jerboa.feat.VoteType
import com.jerboa.ui.theme.SMALL_PADDING
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
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

private const val MAGNET_LINK =
    "magnet:?xt=urn:btih:7ae3a882005bb337885f27610475543834642867" +
        "&dn=Lenin%20-%20Socialism%20and%20Anarchism%20%5Baudiobook" +
        "%5D%20by%20dessalines" +
        "&tr=udp%3A%2F%2Ftracker.theoks.net%3A6969%2Fannounce"

@RunWith(JUnitParamsRunner::class)
class UtilsKtTest {
    @JvmField
    @Rule
    val rule: MockitoRule = MockitoJUnit.rule()

    @Test
    fun testCalculateCommentOffset() {
        assertEquals(0.dp, calculateCommentOffset(0))
        assertEquals(4.dp + SMALL_PADDING, calculateCommentOffset(2))
        assertEquals(8.dp + SMALL_PADDING, calculateCommentOffset(-1))
    }

    @Test
    fun testInstantScores() {
        val initial = InstantScores(0, 0, 0, 0)
        val upvoted = initial.update(VoteType.Upvote)
        assertEquals(1, upvoted.myVote)
        assertEquals(1, upvoted.upvotes)
        assertEquals(0, upvoted.downvotes)
        assertEquals(1, upvoted.score)

        val downvoted = initial.update(VoteType.Downvote)
        assertEquals(-1, downvoted.myVote)
        assertEquals(0, downvoted.upvotes)
        assertEquals(1, downvoted.downvotes)
        assertEquals(-1, downvoted.score)

        val upvotedAgain = downvoted.update(VoteType.Upvote)
        assertEquals(1, upvotedAgain.myVote)
        assertEquals(1, upvotedAgain.upvotes)
        assertEquals(0, upvotedAgain.downvotes)
        assertEquals(1, upvotedAgain.score)

        val downvotedAgain = upvoted.update(VoteType.Downvote)
        assertEquals(-1, downvotedAgain.myVote)
        assertEquals(0, downvotedAgain.upvotes)
        assertEquals(1, downvotedAgain.downvotes)
        assertEquals(-1, downvotedAgain.score)

        val unvoted = upvoted.update(VoteType.Upvote)
        assertEquals(0, unvoted.myVote)
        assertEquals(0, unvoted.upvotes)
        assertEquals(0, unvoted.downvotes)
        assertEquals(0, unvoted.score)

        val unvoted2 = downvoted.update(VoteType.Downvote)
        assertEquals(0, unvoted2.myVote)
        assertEquals(0, unvoted2.upvotes)
        assertEquals(0, unvoted2.downvotes)
        assertEquals(0, unvoted2.score)

        val upvotedAlr = upvoted.copy(myVote = 0).update(VoteType.Upvote)
        assertEquals(1, upvotedAlr.myVote)
        assertEquals(2, upvotedAlr.upvotes)
        assertEquals(0, upvotedAlr.downvotes)
        assertEquals(2, upvotedAlr.score)

        val downvotedAlr = downvoted.copy(myVote = 0).update(VoteType.Downvote)
        assertEquals(-1, downvotedAlr.myVote)
        assertEquals(0, downvotedAlr.upvotes)
        assertEquals(2, downvotedAlr.downvotes)
        assertEquals(-2, downvotedAlr.score)
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
        val ctx =
            mock<Context> {
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
        val ctx =
            mock<Context> {
                on { getString(R.string.url) } doReturn "url"
                on { getString(R.string.url_invalid) } doReturn "url_invalid"
            }

        assertTrue(validateUrl(ctx, "nonsense").hasError)
        assertSame("url_invalid", validateUrl(ctx, "nonsense").label)

        assertFalse(validateUrl(ctx, "").hasError)
        assertFalse(validateUrl(ctx, "https://example.com").hasError)
        assertFalse(validateUrl(ctx, MAGNET_LINK).hasError)
        assertSame("url", validateUrl(ctx, "https://example.com").label)
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
    @Parameters(method = "siFormatCases")
    fun testSiFormat(
        expected: String,
        input: Long,
    ) {
        assertEquals(expected, siFormat(input))
    }

    fun siFormatCases() =
        listOf(
            listOf("0", 0),
            listOf("1K", 1000),
            listOf("1.1K", 1100),
            listOf("1M", 1000000),
            listOf("1.2M", 1234500),
            listOf("12M", 12345000),
        )

    @Test
    fun testParseUrl() {
        val baseUrl = "https://lemmy.ml"
        val cases =
            mapOf(
                "https://feddit.de" to "https://feddit.de",
                "http://example.com" to "http://example.com",
                "/c/community" to "https://lemmy.ml/c/community",
                "/c/community@instance.ml" to "https://instance.ml/c/community",
                "!community@instance.ml" to "https://instance.ml/c/community",
                "!community" to "https://lemmy.ml/c/community",
                "/u/user@instance.ml" to "https://instance.ml/u/user",
                "@user@instance.ml" to "https://instance.ml/u/user",
            )

        cases.forEach { (url, exp) -> assertEquals(exp, parseUrl(baseUrl, url)?.second) }
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
    fun rewriteHttpToHttps() {
        assertEquals("https://example.com", "http://example.com".toHttps())
        assertEquals("https://example.com", "https://example.com".toHttps())
        assertEquals("example.com", "example.com".toHttps())
    }

    @Test
    fun testBuildCommentsTree() {
        val tree1 = buildCommentsTree(listOf(sampleCommentView), null)
        assertEquals(1, tree1.size)
        assertTrue(tree1[0] is CommentNode)

        val sampleCV2 = sampleCommentView.copy(comment = sampleCommentView.comment.copy(path = "0.1.2", id = 2))

        val tree2 = buildCommentsTree(listOf(sampleCommentView, sampleCV2), null)
        assertEquals(1, tree2.size)
        val root2 = tree2[0] as CommentNode
        assertEquals(1, root2.children.size)
        assertEquals(0, root2.depth)
        assertTrue(root2.children[0] is CommentNode)
        assertEquals(root2, root2.children[0].parent)
        assertEquals(1, root2.children[0].depth)

        // Should not generate a missing comment as parent, because we said that root is sampleCV2
        val tree3 = buildCommentsTree(listOf(sampleCV2), sampleCV2.comment.id)
        assertEquals(1, tree3.size)
        assertTrue(tree3[0] is CommentNode)
        val root3 = tree3[0] as CommentNode
        assertEquals(sampleCV2, root3.commentView)
        assertEquals(0, root3.depth)
        assertEquals(0, root3.children.size)

        // Should generate a missing comment as parent
        val tree4 = buildCommentsTree(listOf(sampleCV2), null)
        assertEquals(1, tree4.size)
        assertTrue(tree4[0] is MissingCommentNode)
        val root4 = tree4[0] as MissingCommentNode
        assertEquals(0, root4.depth)
        assertEquals(null, root4.parent)
        assertEquals(1, root4.children.size)
        assertEquals(1, root4.missingCommentView.commentId)
        assertTrue(root4.children[0] is CommentNode)
        val child4 = root4.children[0] as CommentNode
        assertEquals(sampleCV2, child4.commentView)
        assertEquals(1, child4.depth)
        assertEquals(root4, child4.parent)

        val sampleCV5 = sampleCommentView.copy(comment = sampleCommentView.comment.copy(path = "0.1.2.3", id = 3))

        // Confirm recursive missing parent behaviour
        val tree5 = buildCommentsTree(listOf(sampleCV5), null)
        assertEquals(1, tree5.size)
        assertTrue(tree5[0] is MissingCommentNode)
        assertEquals(1, tree5[0].children.size)
        assertTrue(tree5[0].children[0] is MissingCommentNode)
        assertEquals(1, tree5[0].children[0].children.size)
        assertTrue(tree5[0].children[0].children[0] is CommentNode)
        assertEquals(3, tree5[0].children[0].children[0].getId())

        // Confirm that it can generate a missing comment between two comments
        val tree6 = buildCommentsTree(listOf(sampleCommentView, sampleCV5), null)
        assertEquals(1, tree6.size)
        assertTrue(tree6[0] is CommentNode)
        assertEquals(1, tree6[0].children.size)
        assertTrue(tree6[0].children[0] is MissingCommentNode) // The missing comment between sampleCommentView and sampleCV5
        assertEquals(1, tree6[0].children[0].children.size)
        assertTrue(tree6[0].children[0].children[0] is CommentNode)
        assertEquals(3, tree6[0].children[0].children[0].getId())
    }

    @Test
    fun testGetParentPath() {
        assertEquals("0", getParentPath("0.1"))
        assertEquals("0.1", getParentPath("0.1.2"))
        assertEquals("0.1.2", getParentPath("0.1.2.3"))
    }

    @Test
    fun shouldPadUrlWithHttps() {
        assertEquals("https://example.com", "example.com".padUrlWithHttps())
        assertEquals("http://example.com", "http://example.com".padUrlWithHttps())
        assertEquals("https://example.com", "https://example.com".padUrlWithHttps())
        assertEquals("ws://example.com", "ws://example.com".padUrlWithHttps())
        assertEquals(MAGNET_LINK, MAGNET_LINK.padUrlWithHttps())
        assertEquals("", "".padUrlWithHttps())
    }
}
