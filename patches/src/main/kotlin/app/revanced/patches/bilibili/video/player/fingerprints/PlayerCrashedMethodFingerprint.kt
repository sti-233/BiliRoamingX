package app.revanced.patches.bilibili.video.player.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

object W2MethodFingerprint : MethodFingerprint(
    strings = listOf(
        "mPlayCoreService",
        "throwUninitializedPropertyAccessException",
        "setText",
        "setContentDescription"
    ),
    parameters = listOf(),
    returnType = "V"
)
