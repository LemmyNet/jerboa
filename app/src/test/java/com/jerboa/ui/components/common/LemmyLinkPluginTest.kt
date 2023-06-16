package com.jerboa.ui.components.common

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class LemmyLinkPluginTest {
    @Test
    @Parameters(method = "communitySuccessCases")
    fun testCommunityValid(pattern: String, community: String, instance: String?) {
        val matcher = lemmyCommunityPattern.matcher(pattern)

        assertTrue(matcher.find())
        assertEquals(community, matcher.group(1))
        assertEquals(instance, matcher.group(2))
    }

    @Test
    @Parameters(
        value = [
            "a!community",
            "!!community@instance.ml",
            "!co",
        ],
    )
    fun testCommunityInvalid(pattern: String) {
        assertFalse(lemmyCommunityPattern.matcher(pattern).find())
    }

    @Test
    @Parameters(method = "userSuccessCases")
    fun testUserValid(pattern: String, user: String, instance: String?) {
        val matcher = lemmyUserPattern.matcher(pattern)

        assertTrue(matcher.find())
        assertEquals(user, matcher.group(1))
        assertEquals(instance, matcher.group(2))
    }

    @Test
    @Parameters(
        value = [
            "a@user",
            "!@user@instance.ml",
            "@co",
        ],
    )
    fun testUserInvalid(pattern: String) {
        assertFalse(lemmyUserPattern.matcher(pattern).find())
    }

    fun communitySuccessCases() = listOf(
        listOf("!community", "community", null),
        listOf(" !community.", "community", null),
        listOf("!community@instance.ml", "community", "instance.ml"),
        listOf("!community@instance.ml!", "community", "instance.ml"),
    )

    fun userSuccessCases() = listOf(
        listOf("@user", "user", null),
        listOf(" @user.", "user", null),
        listOf("@user@instance.ml", "user", "instance.ml"),
        listOf("@user@instance.ml!", "user", "instance.ml"),
    )
}
