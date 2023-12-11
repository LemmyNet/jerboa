package com.jerboa.util

import android.content.Context
import android.widget.Toast
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.toApiState
import com.jerboa.showBlockCommunityToast
import com.jerboa.showBlockPersonToast
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockCommunity
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockInstanceResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockPerson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun getInstanceFromCommunityUrl(url: String): String {
    return url.substringBefore("/c/")
}


fun blockCommunity(
    scope: CoroutineScope,
    form: BlockCommunity,
    ctx: Context,
) {
    scope.launch {
        val res = API.getInstance().blockCommunity(form).toApiState()
        withContext(Dispatchers.Main) {
            showBlockCommunityToast(res, ctx)
        }
    }
}

fun blockPerson(
    scope: CoroutineScope,
    form: BlockPerson,
    ctx: Context,
) {
    scope.launch {
        val res = API.getInstance().blockPerson(form).toApiState()
        withContext(Dispatchers.Main) {
            showBlockPersonToast(res, ctx)
        }
    }
}


fun showBlockCommunityToast(
    blockInstanceResp: Result<BlockInstanceResponse>,
    instance: String,
    ctx: Context,
) {

    blockInstanceResp
        .onSuccess {
            Toast.makeText(
                ctx,
                ctx.getString(
                    if (it.blocked) {
                        R.string.blocked_community_toast
                    } else {
                        R.string.unblocked_community_toast
                    },
                    instance
                ),
                Toast.LENGTH_SHORT,
            ).show()
        }
        .onFailure {
            Toast.makeText(
                ctx,
                ctx.getText(R.string.instance_block_toast_failure),
                Toast.LENGTH_SHORT,
            ).show()
        }

}