package com.jerboa.feat

import it.vercruysse.lemmyapi.datatypes.MyUserInfo
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PersonView

/**
 * Determines whether someone can admin an item. Uses a hierarchy of admins from the API.
 */
fun canAdmin(
    creatorId: PersonId,
    myUserInfo: MyUserInfo?,
    admins: List<PersonView>?,
    onSelf: Boolean = false,
 ): Boolean {
    val myId = myUserInfo?.local_user_view?.local_user?.person_id

    return myId?.let { myId ->
        if (onSelf && creatorId != myId) {
            false
        } else {
            // You can only do actions on admins created after you
            val firstId = admins?.find { pv -> pv.person.id == creatorId || pv.person.id == myId }?.person?.id

            // IE you should come before them
            val amFirst = firstId == myId
            amFirst
        }
    } ?: run {
        false
    }
 }

fun canModOrAdmin(
    canMod: Boolean,
    creatorId: PersonId,
    myUserInfo: MyUserInfo?,
    admins: List<PersonView>?,
    onSelf: Boolean = false,
): Boolean {
    return canMod || canAdmin(creatorId, myUserInfo, admins, onSelf)
}


// fun amMod(
//    moderators: List<PersonId>?,
//    myId: PersonId,
// ): Boolean = moderators?.contains(myId) ?: false

/**
 * In screens with posts from different communities we don't have access to moderators of those communities
 * So that means that non admin mods can't moderate those posts from that screen
 *
 * So this is QoL were we simulate the mods of the community
 * It is not completely accurate as it doesn't take into account the hierarchy of mods
 * TODO can probably get rid because of canMod
 */
// fun simulateModerators(
//    ctx: Context,
//    account: Account,
//    forCommunity: CommunityId,
// ): List<PersonId> {
//    if (account.isMod) {
//        val siteVM = (ctx.findActivity() as MainActivity).siteViewModel
//        val canModerate = siteVM.moderatedCommunities().orEmpty().contains(forCommunity)
//        if (canModerate) {
//            return listOf(account.id)
//        }
//    }
//    return emptyList()
// }
