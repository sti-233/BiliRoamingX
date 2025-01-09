package app.revanced.patches.bilibili.video.player.patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.bilibili.patcher.patch.MultiMethodBytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.util.proxy.mutableTypes.MutableClass
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.bilibili.video.player.fingerprints.*
import app.revanced.util.exception
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction11x
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction11x
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.reference.ClassReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.immutable.ImmutableList
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference
import com.android.tools.smali.dexlib2.util.Preconditions
import com.google.common.collect.ImmutableList

@Patch(
    name = "SponsorBlock Code",
    description = "SponsorBlock(Code)",
    compatiblePackages = [
        CompatiblePackage(name = "tv.danmaku.bili")
    ]
)
object SponsorBlockPatch : MultiMethodBytecodePatch(
    fingerprints = setOf(
        MiniPlayerProgressFingerprint,
        OGVMiniPlayerServiceFingerprint,
        PlayerContainerCreateFingerprint,
        PlayerContainerDestroyFingerprint,
        PlayerDrawProgressSeekbarFingerprint,
        PlayerUpdateSeekbarThumbTextFingerprint,
        PlaylistUGCMiniPlayerServiceFingerprint,
        PlaylistOGVMiniPlayerServiceFingerprint,
        UGCMiniPlayerServiceFingerprint,
    ),
    multiFingerprints = setOf(PlayerProgressTextWidgetFingerprint),
) {
    override fun execute(context: BytecodeContext) {
        super.execute(context)
        MiniPlayerProgressFingerprint.result?.mutableMethod?.run {
            context.findClass(result.classDef.type)!!.mutableClass.apply {
                methods.add(
                    ImmutableMethod(
                        type,
                        "draw",
                        ImmutableList.of("Landroid/graphics/Canvas;"),
                        "V",
                        AccessFlags.PUBLIC,
                        null,
                        null,
                        ImmutableMethodImplementation(
                            2,
                            ImmutableList.of(
                                BuilderInstruction21c(
                                    Opcode.INVOKE_SUPER,
                                    0,
                                    ImmutableMethodReference(
                                        "Lcom/bilibili/magicasakura/widgets/TintProgressBar;",
                                        "draw",
                                        "(Landroid/graphics/Canvas;)V"
                                    )
                                ),
                                BuilderInstruction21c(
                                    Opcode.INVOKE_STATIC,
                                    1,
                                    ImmutableMethodReference(
                                        "Lapp/revanced/bilibili/patches/sponsorblock/SponsorBlockPatch;",
                                        "onMiniPlayerDrawProgressBar",
                                        "(Landroid/widget/ProgressBar;Landroid/graphics/Canvas;)V"
                                    )
                                ),
                                BuilderInstruction11x(Opcode.RETURN_VOID, 0)
                            ),
                            null,
                            null
                        ),
                    ).toMutable()
                )
            }
        } ?: throw MiniPlayerProgressFingerprint.exception
        OGVMiniPlayerServiceFingerprint.result?.run {
            mutableMethod.cloneMutable(registerCount = 3, clearImplementation = true).apply {
                mutableMethod.name += "_Origin"
                addInstructions(
                    """
                    invoke-static {p1}, Lapp/revanced/bilibili/patches/sponsorblock/SponsorBlockPatch;->onStartMiniPlay(Ljava/lang/Object;)Z
                    move-result v0
                    if-nez v0, :cond_9
                    invoke-direct {p0, p1}, $mutableMethod
                    :cond_9
                    return-void
                """.trimIndent()
                )
            }.also { mutableClass.methods.add(it) }
        } ?: throw OGVMiniPlayerServiceFingerprint.exception
        PlayerContainerCreateFingerprint.result?.run {
            mutableMethod.cloneMutable(registerCount = 2, clearImplementation = true).apply {
                mutableMethod.name += "_Origin"
                addInstructions(
                    """
                    .param p1  # Landroid/os/Bundle;
                        .annotation build Lorg/jetbrains/annotations/Nullable;
                        .end annotation
                    .end param
                    invoke-virtual {p0, p1}, $mutableMethod
                    invoke-static {p0}, Lapp/revanced/bilibili/patches/sponsorblock/SponsorBlockPatch;->onPlayerContainerCreate(Ljava/lang/Object;)V
                    return-void
                """.trimIndent()
                )
            }.also { mutableClass.methods.add(it) }
        } ?: throw PlayerContainerCreateFingerprint.exception
        PlayerContainerDestroyFingerprint.result?.run {
            mutableMethod.cloneMutable(registerCount = 1, clearImplementation = true).apply {
                mutableMethod.name += "_Origin"
                addInstructions(
                    """
                    invoke-virtual {p0}, $mutableMethod
                    invoke-static {p0}, Lapp/revanced/bilibili/patches/sponsorblock/SponsorBlockPatch;->onPlayerContainerDestroy(Ljava/lang/Object;)V
                    return-void
                """.trimIndent()
                )
            }.also { mutableClass.methods.add(it) }
        } ?: throw PlayerContainerDestroyFingerprint.exception
        PlayerDrawProgressSeekbarFingerprint.result?.mutableMethod?.run {
            val instructions = mutableMethod.implementation!!.instructions
            val index = if (instructions[6].opcode == Opcode.INVOKE_VIRTUAL
                && instructions[7].opcode == Opcode.IGET_OBJECT
                && instructions[8].opcode == Opcode.IGET_OBJECT
            ) 9 else 0
            mutableMethod.addInstructionsWithLabels(
                index, """
                invoke-static {p0, p1}, Lapp/revanced/bilibili/patches/sponsorblock/SponsorBlockPatch;->onPlayerDrawProgressSeekbar(Landroid/graphics/drawable/Drawable;Landroid/graphics/Canvas;)V
            """.trimIndent()
            )
        } ?: throw PlayerDrawProgressSeekbarFingerprint.exception
        PlayerUpdateSeekbarThumbTextFingerprint.result?.run {
            val instructions = mutableMethod.implementation!!.instructions
            val index = if (instructions[62].opcode == Opcode.IGET_OBJECT) 62 else 0
            if (index == 62) {
                val instruction = instructions[index] as Instruction21c
                val fieldReference = instruction.reference as FieldReference
                val fieldDescriptor = "${fieldReference.definingClass}->${fieldReference.name}:${fieldReference.type}"
            } else {
                val fieldDescriptor = "Lcom/bilibili/playerbizcommonv2/widget/seek/v3/f;->e:Landroid/widget/TextView;"
                throw PatchException("not found PlayerUpdateSeekbarThumbText method")
            }
            mutableMethod.cloneMutable(registerCount = 4, clearImplementation = true).apply {
                mutableMethod.name += "_Origin"
                addInstructions(
                    """
                    invoke-direct {p0, p1, p2}, $mutableMethod
                    iget-object v0, p0, $fieldDescriptor
                    invoke-static {v0, p1, p2}, Lapp/revanced/bilibili/patches/sponsorblock/SponsorBlockPatch;->onPlayerUpdateSeekbarThumbText(Landroid/widget/TextView;II)V
                    return-void
                """.trimIndent()
                )
            }.also { mutableClass.methods.add(it) }
        } ?: throw PlayerUpdateSeekbarThumbTextFingerprint.exception
        PlaylistUGCMiniPlayerServiceFingerprint.result?.run {
            mutableMethod.cloneMutable(registerCount = 3, clearImplementation = true).apply {
                mutableMethod.name += "_Origin"
                addInstructions(
                    """
                    invoke-static {p1}, Lapp/revanced/bilibili/patches/sponsorblock/SponsorBlockPatch;->onStartMiniPlay(Ljava/lang/Object;)Z
                    move-result v0
                    if-nez v0, :cond_9
                    invoke-direct {p0, p1}, $mutableMethod
                    :cond_9
                    return-void
                """.trimIndent()
                )
            }.also { mutableClass.methods.add(it) }
        } ?: throw PlaylistUGCMiniPlayerServiceFingerprint.exception
        PlaylistOGVMiniPlayerServiceFingerprint.result?.run {
            mutableMethod.cloneMutable(registerCount = 3, clearImplementation = true).apply {
                mutableMethod.name += "_Origin"
                addInstructions(
                    """
                    invoke-static {p1}, Lapp/revanced/bilibili/patches/sponsorblock/SponsorBlockPatch;->onStartMiniPlay(Ljava/lang/Object;)Z
                    move-result v0
                    if-nez v0, :cond_9
                    invoke-direct {p0, p1}, $mutableMethod
                    :cond_9
                    return-void
                """.trimIndent()
                )
            }.also { mutableClass.methods.add(it) }
        } ?: throw PlaylistOGVMiniPlayerServiceFingerprint.exception
        UGCMiniPlayerServiceFingerprint.result?.run {
            mutableMethod.cloneMutable(registerCount = 3, clearImplementation = true).apply {
                mutableMethod.name += "_Origin"
                addInstructions(
                    """
                    invoke-static {p1}, Lapp/revanced/bilibili/patches/sponsorblock/SponsorBlockPatch;->onStartMiniPlay(Ljava/lang/Object;)Z
                    move-result v0
                    if-nez v0, :cond_9
                    invoke-direct {p0, p1}, $mutableMethod
                    :cond_9
                    return-void
                """.trimIndent()
                )
            }.also { mutableClass.methods.add(it) }
        } ?: throw UGCMiniPlayerServiceFingerprint.exception
        PlayerProgressTextWidgetFingerprint.result.ifEmpty {
            throw PlayerProgressTextWidgetFingerprint.exception
        }.forEach { r ->
            val originMethod = r.mutableMethod
            originMethod.cloneMutable(registerCount = 3, clearImplementation = true).apply {
                originMethod.name += "_Origin"
                addInstructions(
                    """
                    invoke-virtual {p0, p1, p2}, $originMethod
                    invoke-static {p0, p1, p2}, Lapp/revanced/bilibili/patches/sponsorblock/SponsorBlockPatch;->onPlayerUpdateProgressText(Landroid/widget/TextView;II)V
                    return-void
                """.trimIndent()
                )
            }.also { r.mutableClass.methods.add(it) }
        }
    }
}
