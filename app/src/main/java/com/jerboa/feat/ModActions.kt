package com.jerboa.feat

import it.vercruysse.lemmyapi.v0x19.datatypes.CommunityModeratorView
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonId
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonView
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Determines whether someone can moderate an item. Uses a hierarchy of admins then mods.
 */
fun canMod(
    creatorId: PersonId,
    admins: List<PersonView>?,
    moderators: List<CommunityModeratorView>?,
    myId: PersonId?,
    onSelf: Boolean = false,
): Boolean {
    return if (myId !== null) {
        // You can do moderator actions only on the mods added after you.
        val adminIds = admins?.map { a -> a.person.id }.orEmpty()
        val modIds = moderators?.map { m -> m.moderator.id }.orEmpty()

        val adminsThenMods = adminIds.toMutableList()
        adminsThenMods.addAll(modIds)

        val myIndex = adminsThenMods.indexOf(myId)
        if (myIndex == -1) {
            false
        } else {
            // onSelf +1 on mod actions not for yourself, IE ban, remove, etc
            val subList = adminsThenMods.subList(0, myIndex.plus(if (onSelf) 0 else 1))

            !subList.contains(creatorId)
        }
    } else {
        false
    }
}

fun futureDaysToUnixTime(days: Long?): Long? {
    return days?.let {
        Instant.now().plus(it, ChronoUnit.DAYS).epochSecond
    }
}

fun amMod(
    moderators: List<CommunityModeratorView>?,
    myId: PersonId?,
): Boolean {
    return moderators?.map { it.moderator.id }?.contains(myId) ?: false
}

fun amAdmin(
    admins: List<PersonView>?,
    myId: PersonId?,
): Boolean {
    return admins?.map { it.person.id }?.contains(myId) ?: false
}
