package app.revanced.patches.bilibili.video.player.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

object PlaylistMiniPlayerServiceFingerprint : MethodFingerprint(
    strings = listOf("theseus-playlist", "tryStartMiniPlayerPlay, ", "PlaylistUGCMiniPlayerService"),
    parameters = listOf("Lcom/bilibili/ship/theseus/united/page/miniplayer/DetailMiniPlayerService$b;"),
    returnType = "V"
)