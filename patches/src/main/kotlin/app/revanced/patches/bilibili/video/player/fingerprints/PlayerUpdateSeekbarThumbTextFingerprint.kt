package app.revanced.patches.bilibili.video.player.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

object PlayerUpdateSeekbarThumbTextFingerprint : MethodFingerprint(
    strings = listOf("tvMessage", " / "),
    parameters = listOf("II"),
    returnType = "V"
)