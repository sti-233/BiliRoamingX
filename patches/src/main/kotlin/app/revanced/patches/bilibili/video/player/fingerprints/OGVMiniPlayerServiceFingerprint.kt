package app.revanced.patches.bilibili.video.player.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

object OGVMiniPlayerServiceFingerprint : MethodFingerprint(
    strings = listOf("theseus-ogv", "tryStartMiniPlayerPlay"),
    returnType = "V"
)