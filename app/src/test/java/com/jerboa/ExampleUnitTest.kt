package com.jerboa

import com.jerboa.api.APIService
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.api.GetPosts
import com.jerboa.datatypes.api.GetSite
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testGetSite() = runBlocking {
    val api = APIService.getInstance()
    val form = GetSite(null)
    val out = api.getSite(form.serializeToMap())

    assertEquals("Lemmy", out.site_view!!.site.name)
  }

  @Test
  fun testGetPosts() = runBlocking {
    // TODO
    val api = APIService.getInstance()
    val form = GetPosts(
      ListingType.All.toString(),
      SortType.Active.toString(),
      null,
      null,
      null,
      null,
      null,
      null
    )
    val out = api.getPosts(form.serializeToMap())
    println(out.posts[0])
    assertNotNull(out.posts)
  }
}
