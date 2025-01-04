package app.revanced.patches.bilibili.video.player.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

object PlayerContainerDestroyFingerprint : MethodFingerprint(
    strings = listOf("mPlayerServiceManager", "mActivityStateService"),
    parameters = listOf(""),
    returnType = "V"
)