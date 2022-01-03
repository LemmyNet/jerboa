package com.jerboa.ui.components.post

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.APIService
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.api.GetPosts
import com.jerboa.serializeToMap
import kotlinx.coroutines.launch

class PostListingsViewModel : ViewModel() {

  private val api = APIService.getInstance()
  var posts: List<PostView> by mutableStateOf(listOf())
    private set
  var loading: Boolean by mutableStateOf(false)
    private set

  lateinit var clickedPost: PostView

  fun fetchPosts(form: GetPosts) {
    viewModelScope.launch {
      try {
        loading = true
        val res = api.getPosts(form = form.serializeToMap())
        posts = res.posts
      } catch (e: Exception) {
        Log.e("ViewModel: PostListingsViewModel", e.toString())
      } finally {
        loading = false
      }
    }
  }

  fun onPostClicked(post: PostView) {
    clickedPost = post
  }
}
