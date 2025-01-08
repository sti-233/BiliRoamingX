package app.revanced.patches.bilibili.misc.notification.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

object NotificationStyleAbFingerprint : MethodFingerprint(
    strings = listOf("dd_enable_system_media_control"),
    returnType = "Ljava/lang/Boolean;",
    parameters = listOf()
)
