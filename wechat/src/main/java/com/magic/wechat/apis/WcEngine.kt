package com.magic.wechat.apis

import com.magic.kernel.core.HookerCenter
import com.magic.wechat.hookers.FinderLiveHookers

object WcEngine {
    var hookerCenters: List<HookerCenter> = listOf(
        FinderLiveHookers
//        ApplicationHookers,
//        ContactHookers,
//        ConversationHookers,
//        CustomerHookers,
//        NotificationHookers
//        DepartmentHookers
//        UserLabelHookers
    )
}