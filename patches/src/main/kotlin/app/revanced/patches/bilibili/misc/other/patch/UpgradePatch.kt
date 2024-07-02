package app.revanced.patches.bilibili.misc.other.patch

// 导入所需的包和类
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.bilibili.misc.other.fingerprints.AttachChannelInfoFingerprint
import app.revanced.patches.bilibili.misc.other.fingerprints.MainCommonServiceImplFingerprint
import app.revanced.patches.bilibili.utils.className
import app.revanced.util.exception
import com.android.tools.smali.dexlib2.Opcode

// 使用Patch注解来定义补丁的元数据，包括名称、描述和兼容的包名
@Patch(
    name = "Upgrade", // 补丁名称
    description = "自定义更新辅助补丁", // 补丁描述
    compatiblePackages = [ // 兼容的应用包名列表
        CompatiblePackage(name = "tv.danmaku.bili"), // 哔哩哔哩
        CompatiblePackage(name = "tv.danmaku.bilibilihd"), // 哔哩哔哩HD版
        CompatiblePackage(name = "com.bilibili.app.in") // 哔哩哔哩国际版
    ]
)
// 定义一个对象UpgradePatch，它是一个BytecodePatch，传入两个Fingerprint对象
object UpgradePatch : BytecodePatch(setOf(AttachChannelInfoFingerprint, MainCommonServiceImplFingerprint)) {
    // 重写execute方法，该方法用于执行补丁操作，传入一个BytecodeContext上下文对象
    override fun execute(context: BytecodeContext) {
        // 从AttachChannelInfoFingerprint结果中获取可变方法，并在第一个位置插入"return-void"指令
        AttachChannelInfoFingerprint.result?.mutableMethod?.addInstructions(
            0, "return-void" // 在方法的第0个位置插入返回指令
        ) ?: throw AttachChannelInfoFingerprint.exception // 如果结果为空，则抛出AttachChannelInfoFingerprint的异常
        
        // 查找MainCommonServiceImplFingerprint结果的classDef中符合特定条件的方法
        MainCommonServiceImplFingerprint.result?.classDef?.methods?.find {
            it.returnType == "V" && it.parameterTypes == listOf("Landroid/app/Activity;") // 返回类型为void且参数为Activity的方法
        }?.let { m -> // 如果找到了符合条件的方法
            // 查找并获取AboutFragment类的可变类对象
            context.findClass(
                "Lapp/revanced/bilibili/settings/fragments/AboutFragment;"
            )!!.mutableClass.run {
                // 查找类中的构造方法
                methods.first { it.name == "<init>" }.run {
                    // 找到构造方法中return-void指令的位置
                    val returnIndex = implementation!!.instructions.indexOfFirst {
                        it.opcode == Opcode.RETURN_VOID
                    }
                    // 获取AboutFragment类中的checkUpdateMethod字段
                    val field = fields.first { it.name == "checkUpdateMethod" }
                    // 在return指令之前插入新的指令，设置checkUpdateMethod字段的值
                    addInstructions(
                        returnIndex, """
                        const-string v0, "${m.definingClass.className}#${m.name}"
                        iput-object v0, p0, $field
                    """.trimIndent()
                    )
                }
            }
        } ?: throw MainCommonServiceImplFingerprint.exception // 如果没有找到符合条件的方法，则抛出MainCommonServiceImplFingerprint的异常
    }
}