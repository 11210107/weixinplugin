package com.magic.wechat.apis

import com.magic.kernel.core.HookerCenter
import com.magic.wechat.hookers.ScanQRHooker

object ByteDanceEngine {
    var hookerCenters: List<HookerCenter> = listOf(
        ScanQRHooker
//        ApplicationHookers,
//        ContactHookers,
//        ConversationHookers,
//        CustomerHookers,
//        NotificationHookers
//        DepartmentHookers
//        UserLabelHookers
    )
}