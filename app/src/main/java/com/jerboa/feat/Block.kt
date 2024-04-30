package com.jerboa.feat

import android.content.Context
import android.widget.Toast
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockCommunity
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockCommunityResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockInstanceResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockPerson
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockPersonResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.Instance
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

private fun makeSuccessfulBlockMessage(
    isBlocked: Boolean,
    name: String,
    context: Context,
) {
    Toast.makeText(
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

fun showBlockInstanceToast(
    blockInstanceResp: Result<BlockInstanceResponse>,
    instance: String,
    ctx: Context,
) {
    blockInstanceResp
        .onSuccess {
            makeSuccessfulBlockMessage(
                isBlocked = it.blocked,
                name = instance,
                context = ctx,
            )
        }
        .onFailure {
            Toast.makeText(
                ctx,
                ctx.getText(R.string.instance_block_toast_failure),
                Toast.LENGTH_SHORT,
            ).show()
        }
}

fun showBlockPersonToast(
    blockPersonRes: ApiState<BlockPersonResponse>,
    ctx: Context,
) {
    when (blockPersonRes) {
        is ApiState.Success -> makeSuccessfulBlockMessage(
            isBlocked = blockPersonRes.data.blocked,
            name = blockPersonRes.data.person_view.person.name,
            context = ctx,
        )

        else -> Toast.makeText(
            ctx,
            ctx.getText(R.string.user_block_toast_failure),
            Toast.LENGTH_SHORT,
        ).show()
    }
}

fun showBlockInstanceToast(
    blockInstanceResponse: ApiState<BlockInstanceResponse>,
    instance: Instance,
    ctx: Context,
) {
    when (blockInstanceResponse) {
        is ApiState.Success -> makeSuccessfulBlockMessage(
            isBlocked = blockInstanceResponse.data.blocked,
            name = instance.domain,
            context = ctx,
        )

        else -> Toast.makeText(
            ctx,
            ctx.getText(R.string.instance_block_toast_failure),
            Toast.LENGTH_SHORT,
        ).show()
    }
}

fun showBlockCommunityToast(
    blockCommunityRes: ApiState<BlockCommunityResponse>,
    ctx: Context,
) {
    when (blockCommunityRes) {
        is ApiState.Success -> makeSuccessfulBlockMessage(
            isBlocked = blockCommunityRes.data.blocked,
            name = blockCommunityRes.data.community_view.community.name,
            context = ctx,
        )

        else -> Toast.makeText(
            ctx,
            ctx.getText(R.string.community_block_toast_failure),
            Toast.LENGTH_SHORT,
        ).show()
    }
}
