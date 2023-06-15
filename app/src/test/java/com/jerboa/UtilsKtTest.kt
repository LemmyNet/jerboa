package com.jerboa

import androidx.compose.ui.unit.dp
import com.jerboa.datatypes.api.GetUnreadCountResponse
import com.jerboa.ui.theme.SMALL_PADDING
import org.junit.Assert.*
import org.junit.Test

class UtilsKtTest {
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
        assertTrue(validatePostName("").hasError)
        assertTrue(validatePostName("a").hasError)
        assertTrue(validatePostName("a".repeat(MAX_POST_TITLE_LENGTH)).hasError)

        assertFalse(validatePostName("totally fine").hasError)
        assertFalse(validatePostName("a".repeat(MAX_POST_TITLE_LENGTH - 1)).hasError)
    }

    @Test
    fun testValidateUrl() {
        assertTrue(validateUrl("nonsense").hasError)

        assertFalse(validateUrl("").hasError)
        assertFalse(validateUrl("https://example.com").hasError)
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
                "http://localhost:8535/pictrs/image/file.png?thumbnail=256&format=jpg",
                3,
            ),
        )
    }

    @Test
    fun testUnreadCountTotal() {
        assertEquals(6, unreadCountTotal(GetUnreadCountResponse(1, 2, 3)))
    }

    @Test
    fun testSiFormat() {
        assertEquals("0", siFormat(0))
        assertEquals("1K", siFormat(1000))
        assertEquals("1.1K", siFormat(1100))
        assertEquals("1M", siFormat(1000000))
        assertEquals("1.2M", siFormat(1234500))
        assertEquals("12M", siFormat(12345000))
    }
}
