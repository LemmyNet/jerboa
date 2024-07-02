package com.jerboa.feat

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.jerboa.R
import com.jerboa.api.API
import it.vercruysse.lemmyapi.datatypes.BlockCommunity
import it.vercruysse.lemmyapi.datatypes.BlockCommunityResponse
import it.vercruysse.lemmyapi.datatypes.BlockInstanceResponse
import it.vercruysse.lemmyapi.datatypes.BlockPerson
import it.vercruysse.lemmyapi.datatypes.BlockPersonResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun getInstanceFromCommunityUrl(url: String): String = url.substringBefore("/c/")

fun blockCommunity(
    scope: CoroutineScope,
    form: BlockCommunity,
    ctx: Context,
) {
    scope.launch {
        val res = API.getInstance().blockCommunity(form)
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
        val res = API.getInstance().blockPerson(form)
        withContext(Dispatchers.Main) {
            showBlockPersonToast(res, ctx)
        }
    }
}

fun showBlockInstanceToast(
    blockInstanceResp: Result<BlockInstanceResponse>,
    instance: String,
    ctx: Context,
) {
    blockInstanceResp
        .onSuccess {
            makeSuccessfulBlockMessage(
                it.blocked,
                instance,
                ctx,
            )
        }.onFailure {
            Toast
                .makeText(
                    ctx,
                    ctx.getText(R.string.instance_block_toast_failure),
                    Toast.LENGTH_SHORT,
                ).show()
            Log.i("Block", "failed", it)
        }
}

fun showBlockPersonToast(
    blockPersonRes: Result<BlockPersonResponse>,
    ctx: Context,
) {
    blockPersonRes
        .onSuccess {
            makeSuccessfulBlockMessage(
                it.blocked,
                it.person_view.person.name,
                ctx,
            )
        }.onFailure {
            Toast
                .makeText(
                    ctx,
                    ctx.getText(R.string.user_block_toast_failure),
                    Toast.LENGTH_SHORT,
                ).show()
            Log.i("Block", "failed", it)
        }
}

fun showBlockCommunityToast(
    blockCommunityRes: Result<BlockCommunityResponse>,
    ctx: Context,
) {
    blockCommunityRes
        .onSuccess {
            makeSuccessfulBlockMessage(
                it.blocked,
                it.community_view.community.name,
                ctx,
            )
        }.onFailure {
            Toast
                .makeText(
                    ctx,
                    ctx.getText(R.string.community_block_toast_failure),
                    Toast.LENGTH_SHORT,
                ).show()
            Log.i("Block", "failed", it)
        }
}

private fun makeSuccessfulBlockMessage(
    isBlocked: Boolean,
    name: String,
    context: Context,
) {
    Toast
        .makeText(
            context,
            context.getString(
                if (isBlocked) {
                    R.string.blocked_element_toast
                } else {
                    R.string.unblocked_element_toast
                },
                name,
            ),
            Toast.LENGTH_SHORT,
        ).show()
}
