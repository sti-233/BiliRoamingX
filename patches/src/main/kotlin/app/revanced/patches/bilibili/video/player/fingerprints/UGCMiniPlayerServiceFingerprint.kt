package app.revanced.patches.bilibili.video.player.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

object UGCMiniPlayerServiceFingerprint : MethodFingerprint(
    strings = listOf("theseus-ugc", "tryStartMiniPlayerPlay, "),
    returnType = "V"
)