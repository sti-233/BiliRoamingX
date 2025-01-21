package app.revanced.patches.bilibili.video.player.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

object PlaylistUGCMiniPlayerServiceFingerprint : MethodFingerprint(
    strings = listOf("theseus-playlist", "tryStartMiniPlayerPlay, ", "PlaylistUGCMiniPlayerService"),
    returnType = "V"
)