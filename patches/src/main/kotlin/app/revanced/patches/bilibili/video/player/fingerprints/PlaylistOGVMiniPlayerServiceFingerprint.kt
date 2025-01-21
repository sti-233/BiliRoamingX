package app.revanced.patches.bilibili.video.player.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

object PlaylistOGVMiniPlayerServiceFingerprint : MethodFingerprint(
    strings = listOf("theseus-playlist", "tryStartMiniPlayerPlay, ", "PlaylistOGVMiniPlayerService"),
    returnType = "V"
)