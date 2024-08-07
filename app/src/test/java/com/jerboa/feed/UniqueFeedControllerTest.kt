package com.jerboa.feed

import it.vercruysse.lemmyapi.Identity
import org.junit.Assert.*
import org.junit.Test

class UniqueFeedControllerTest {
    private data class PostView(
        override val id: Long,
    ) : Identity

    @Test
    fun `Should not add duplicate posts`() {
        val controller = UniqueFeedController<PostView>()
        controller.add(PostView(1))
        assertEquals(1, controller.feed.size)
        controller.add(PostView(1))
        assertEquals(1, controller.feed.size)
        controller.add(PostView(2))
        assertEquals(2, controller.feed.size)
    }

    @Test
    fun `Should remove post`() {
        val controller = UniqueFeedController<PostView>()
        controller.add(PostView(1))
        assertEquals(1, controller.feed.size)
        controller.remove(PostView(1))
        assertEquals(0, controller.feed.size)
    }

    @Test
    fun `Post removal should clear id`() {
        val controller = UniqueFeedController<PostView>()
        controller.add(PostView(1))
        assertEquals(1, controller.feed.size)
        controller.remove(PostView(1))
        assertTrue(controller.feed.isEmpty())
        controller.add(PostView(1))
        assertEquals(1, controller.feed.size)
    }

    @Test
    fun `Should clear all posts`() {
        val controller = UniqueFeedController<PostView>()
        controller.add(PostView(1))
        controller.add(PostView(2))
        assertEquals(2, controller.feed.size)
        controller.clear()
        assertTrue(controller.feed.isEmpty())
    }

    @Test
    fun `Clear should clear ids`() {
        val controller = UniqueFeedController<PostView>()
        controller.add(PostView(1))
        controller.add(PostView(2))
        assertEquals(2, controller.feed.size)
        controller.clear()
        assertTrue(controller.feed.isEmpty())
        controller.add(PostView(1))
        controller.add(PostView(2))
        assertEquals(2, controller.feed.size)
    }

    @Test
    fun `Add all should not add duplicates`() {
        val controller = UniqueFeedController<PostView>()
        controller.addAll(listOf(PostView(1), PostView(2), PostView(1)))
        assertEquals(2, controller.feed.size)
    }

    @Test
    fun `Init should clear ids`() {
        val controller = UniqueFeedController<PostView>()
        controller.add(PostView(1))
        controller.add(PostView(2))
        assertEquals(2, controller.feed.size)
        controller.init(listOf(PostView(1), PostView(2)))
        assertEquals(2, controller.feed.size)
    }
}
