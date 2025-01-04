package app.revanced.patches.bilibili.video.player.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object PlayerDrawProgressSeekbarFingerprint : MethodFingerprint(
    parameters = listOf("Landroid/graphics/Canvas;"),
    returnType = "V",
    opcodes = listOf(
        Opcode.IGET_OBJECT,
        Opcode.IGET,
        Opcode.INVOKE_VIRTUAL,
        Opcode.IGET_OBJECT,
        Opcode.IGET,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.IGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.INVOKE_DIRECT,
        Opcode.RETURN_VOID
    ),
    customFingerprint = { methodDef, classDef ->
        (methodDef.name == "draw") && classDef.run {
            superclass == "Landroid/graphics/drawable/Drawable;" && interfaces.size == 0
        }
    }
)