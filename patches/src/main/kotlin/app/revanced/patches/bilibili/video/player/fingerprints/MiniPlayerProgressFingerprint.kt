package app.revanced.patches.bilibili.video.player.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object MiniPlayerProgressFingerprint : MethodFingerprint(
    returnType = "V",
    opcodes = listOf(
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.IF_EQZ,
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT_WIDE,
        Opcode.LONG_TO_INT,
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT_WIDE,
        Opcode.LONG_TO_INT,
        Opcode.IF_LTZ,
        Opcode.IF_LEZ,
        Opcode.IF_LE,
        Opcode.MOVE,
        Opcode.INVOKE_VIRTUAL,
        Opcode.INVOKE_VIRTUAL,
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT_WIDE,
        Opcode.LONG_TO_INT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.RETURN_VOID
    ),
    customFingerprint = { methodDef, classDef ->
        classDef.run {
            superclass == "Lcom/bilibili/magicasakura/widgets/TintProgressBar;" && interfaces.size == 3
        }
    }
)
