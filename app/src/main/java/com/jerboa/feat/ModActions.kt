package com.jerboa.feat

import android.content.Context
import com.jerboa.MainActivity
import com.jerboa.db.entity.Account
import com.jerboa.findActivity
import it.vercruysse.lemmyapi.datatypes.CommunityId
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PersonView
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Determines whether someone can moderate an item. Uses a hierarchy of admins then mods.
 */
fun canMod(
    creatorId: PersonId,
    admins: List<PersonView>?,
    moderators: List<PersonId>?,
    myId: PersonId,
    onSelf: Boolean = false,
): Boolean {
    // You can do moderator actions only on the mods added after you.
    val adminIds = admins?.map { a -> a.person.id }.orEmpty()
    val modIds = moderators.orEmpty()

    val adminsThenMods = adminIds.toMutableList()
    adminsThenMods.addAll(modIds)

    val myIndex = adminsThenMods.indexOf(myId)
    return if (myIndex == -1) {
        false
    } else {
        // onSelf +1 on mod actions not for yourself, IE ban, remove, etc
        val subList = adminsThenMods.subList(0, myIndex.plus(if (onSelf) 0 else 1))

        !subList.contains(creatorId)
    }
}

fun futureDaysToUnixTime(days: Long?): Long? =
    days?.let {
        Instant.now().plus(it, ChronoUnit.DAYS).epochSecond
    }

fun amMod(
    moderators: List<PersonId>?,
    myId: PersonId,
): Boolean = moderators?.contains(myId) ?: false

/**
 * In screens with posts from different communities we don't have access to moderators of those communities
 * So that means that non admin mods can't moderate those posts from that screen
 *
 * So this is QoL were we simulate the mods of the community
 * It is not completely accurate as it doesn't take into account the hierarchy of mods
 */
fun simulateModerators(
    ctx: Context,
    account: Account,
    forCommunity: CommunityId,
): List<PersonId> {
    if (account.isMod) {
        val siteVM = (ctx.findActivity() as MainActivity).siteViewModel
        val canModerate = siteVM.moderatedCommunities().orEmpty().contains(forCommunity)
        if (canModerate) {
            return listOf(account.id)
        }
    }
    return emptyList()
}
