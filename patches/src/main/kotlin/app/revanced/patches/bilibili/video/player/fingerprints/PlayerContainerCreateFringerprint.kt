package app.revanced.patches.bilibili.video.player.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

object PlayerContainerCreateFingerprint : MethodFingerprint(
    strings = listOf("mVideosPlayDirectorService", "new player container create"),
    parameters = listOf("Landroid/os/Bundle;"),
    returnType = "V"
)