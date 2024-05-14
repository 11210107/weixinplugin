package com.magic.xmagichooker.model


data class FinderList(
    val acctStatus: Int,
    val finderList: List<Finder>
)
data class Finder(
    val acctType: Int,
    val adminNickname: String,
    val anchorStatusFlag: String,
    val authIconType: Int,
    val categoryFlag: String,
    val coverImgUrl: String?,
    val finderUin: String,
    val finderUsername: String,
    val headImgUrl: String?,
    val isMasterFinder: Boolean,
    val liveStatus: Int,
    val nickname: String,
    val ownerWxUin: Long,
    val spamFlag: Int,
    val uniqId: String
)