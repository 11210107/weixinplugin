package com.magic.xmagichooker.model

data class TencentBaseResult<T>(
    val data: T,
    val errCode: Int,
    val errMsg: String
){
    val isSuccess:Boolean
        get() = errCode == 0

}