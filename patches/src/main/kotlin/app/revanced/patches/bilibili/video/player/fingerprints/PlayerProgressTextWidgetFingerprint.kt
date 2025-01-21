package app.revanced.patches.bilibili.video.player.fingerprints

import app.revanced.patches.bilibili.patcher.fingerprint.MultiMethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object PlayerProgressTextWidgetFingerprint : MultiMethodFingerprint(
    parameters = listOf("II"),
    returnType = "V",
    opcodes = listOf(
        Opcode.SGET_OBJECT,
        Opcode.INT_TO_LONG,
        Opcode.CONST_4,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT,
        Opcode.CONST_STRING,
        Opcode.IF_EQZ,
        Opcode.MOVE_OBJECT,
        Opcode.INT_TO_LONG,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT,
        Opcode.IF_EQZ,
        Opcode.GOTO,
        Opcode.MOVE_OBJECT,
        Opcode.NEW_INSTANCE,
        Opcode.INVOKE_DIRECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.CONST_16,
        Opcode.INVOKE_VIRTUAL,
        Opcode.INVOKE_VIRTUAL,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.RETURN_VOID
    ),
    customFingerprint = { methodDef, classDef ->
        (methodDef.name == "updateTime") && classDef.run {
            superclass == "Landroidx/appcompat/widget/AppCompatTextView;" && interfaces.size == 0
        }
    }
)