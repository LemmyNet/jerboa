package com.jerboa.ui.components.community

import android.content.Context
import android.widget.Toast
import com.jerboa.api.blockCommunityWrapper
import com.jerboa.datatypes.CommunitySafe
import com.jerboa.datatypes.api.BlockCommunity
import com.jerboa.db.Account
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun blockCommunityRoutine(
    community: CommunitySafe,
    block: Boolean,
    account: Account,
    ctx: Context,
    scope: CoroutineScope
) {
    scope.launch {
        val form = BlockCommunity(community.id, block, account.jwt)
        blockCommunityWrapper(form, ctx)
        Toast.makeText(ctx, "${community.name} Blocked", Toast.LENGTH_SHORT).show()
    }
}
