package com.magic.xmagichooker.model

data class ScanQRLoginTask(
    val eventType: String?,
    val platform: String?,
    val qrcodeB64: String?,
    val taskId: String?,
    val qrcodeDecodeRaw: String?
)