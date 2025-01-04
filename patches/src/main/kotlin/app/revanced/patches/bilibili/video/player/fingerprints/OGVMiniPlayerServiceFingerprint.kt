package app.revanced.patches.bilibili.video.player.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

object OGVMiniPlayerServiceFingerprint : MethodFingerprint(
    strings = listOf("theseus-ogv", "tryStartMiniPlayerPlay"),
    parameters = listOf("Lcom/bilibili/ship/theseus/united/page/miniplayer/DetailMiniPlayerService$b;"),
    returnType = "V"
)