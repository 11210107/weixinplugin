package com.magic.wechat.hookers.interfaces

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View

interface IFinderLiveHooker {
    /**
     * onEnterFinderLive
     */
//    fun onEnterFinderLiveVisitorUI(r24:Intent,r25:Context,r26:Long,r28:Long,r29:String,r30:String,r31:String,r32:String,r33:String,r34:String,r35:Int,r36:String,r37:Int,r38:Boolean) {}
    fun onCreate(r9:Bundle) {}
    fun onResume(uiComponent:Any) {}
    fun getFollowContact(followListPresent:Any,aVar:Any,pVar:Any) {}
    fun jumpToLive(baseFinderFeed:Any,i2:Int) {}
    fun onFetchDone(finderProfileFeedLoader:Any,iResponse:Any) {}
    fun getFeedLoader(finderProfileFeedUIC:Any) {}
//    fun onViewCreated(view: View, bundle:Bundle) {}
    fun onFragmentResume(fragment: Any) {}
}