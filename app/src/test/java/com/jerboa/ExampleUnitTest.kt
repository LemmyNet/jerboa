package com.jerboa

import com.jerboa.api.API
import com.jerboa.api.DEFAULT_INSTANCE
import it.vercruysse.lemmyapi.datatypes.GetPost
import it.vercruysse.lemmyapi.datatypes.GetPosts
import it.vercruysse.lemmyapi.dto.ListingType
import it.vercruysse.lemmyapi.dto.SortType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@Ignore
class ExampleUnitTest {
    @Before
    fun init_api() {
        runBlocking { API.setLemmyInstance(DEFAULT_INSTANCE) }
    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testGetSite() =
        runBlocking {
            val api = API.getInstance()
            val out = api.getSite().getOrThrow()

            assertEquals("Lemmy", out.site_view.site.name)
        }

    @Test
    fun testGetPosts() =
        runBlocking {
            val api = API.getInstance()
            val form =
                GetPosts(
                    ListingType.All,
                    SortType.Active,
                )
            val out = api.getPosts(form).getOrThrow()
            assertNotNull(out.posts)
        }

    @Test
    fun testGetPost() =
        runBlocking {
            val api = API.getInstance()
            val form =
                GetPost(
                    id = 139549,
                )
            val out = api.getPost(form).getOrThrow()
            assertNotNull(out)
        }

    /*
        @Test
        fun testLogin() = runBlocking {
            val api = API.getInstance()
            val form = Login(username_or_email = "tester12345", password = "tester12345")
            val out = api.login(form).body()!!
            assertNotNull(out.jwt)
        }
     */
}
