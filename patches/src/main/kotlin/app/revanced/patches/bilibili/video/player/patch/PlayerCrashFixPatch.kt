package app.revanced.patches.bilibili.video.player.patch

import app.revanced.patcher.data.Method
import app.revanced.patcher.patch.Patch
import app.revanced.patcher.patch.PatchContext
import app.revanced.patches.bilibili.video.player.fingerprints.PlayerCrashedMethodFingerprint

class W2MethodPatch : Patch {
    override fun onApply(context: PatchContext) {
        val method = context.findMethod(W2MethodFingerprint)
            ?: throw IllegalStateException("Failed to find the method 'w2'.")
        val originalCode = method.instructions
        method.instructions.clear()
        method.instructions.addAll(originalCode)
    }
}