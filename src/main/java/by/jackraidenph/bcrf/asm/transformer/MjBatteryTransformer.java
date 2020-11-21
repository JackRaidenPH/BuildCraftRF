package by.jackraidenph.bcrf.asm.transformer;

import net.thesilkminer.mc.fermion.asm.api.LaunchPlugin;
import net.thesilkminer.mc.fermion.asm.api.descriptor.ClassDescriptor;
import net.thesilkminer.mc.fermion.asm.api.transformer.TransformerData;
import net.thesilkminer.mc.fermion.asm.common.utility.Log;
import net.thesilkminer.mc.fermion.asm.prefab.AbstractTransformer;
import org.objectweb.asm.*;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public class MjBatteryTransformer extends AbstractTransformer {

    private static final Log LOGGER = Log.of("MjBattery");

    public MjBatteryTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("mj_battery_transformer")
                        .setDescription("Patches MjBattery to implement Forge Energy")
                        .build(),
                ClassDescriptor.of("buildcraft.api.mj.MjBattery")
        );
        receiveEnergyMethodInjector.class.toString();
        extractEnergyMethodInjector.class.toString();
        getEnergyStoredMethodInjector.class.toString();
        getMaxEnergyStoredMethodInjector.class.toString();
        canExtractMethodInjector.class.toString();
        canReceiveMethodInjector.class.toString();
    }

    @Nonnull
    @Override
    public BiFunction<Integer, ClassVisitor, ClassVisitor> getClassVisitorCreator() {
        return (v, cw) -> new ClassVisitor(v, cw) {

            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                super.visit(52, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, "buildcraft/api/mj/MjBattery", "Ljava/lang/Object;Lnet/minecraftforge/common/util/INBTSerializable<Lnet/minecraft/nbt/NBTTagCompound;>;Lnet/minecraftforge/energy/IEnergyStorage;", "java/lang/Object", new String[]{"net/minecraftforge/common/util/INBTSerializable", "net/minecraftforge/energy/IEnergyStorage"});
            }

            @Override
            public void visitEnd() {

                LOGGER.i("      GOD MACHINE HAVE MERCY UPON MY MORTAL SOUL...");

                final FieldVisitor toMjVisitor = super.visitField(Opcodes.ACC_PRIVATE, "toMJ", "D", null, null);
                toMjVisitor.visitEnd();

                final FieldVisitor fromMjVisitor = super.visitField(Opcodes.ACC_PRIVATE, "fromMJ", "D", null, null);
                fromMjVisitor.visitEnd();

                final MethodVisitor receiveEnergyVisitor = super.visitMethod(Opcodes.ACC_PUBLIC, "receiveEnergy", "(IZ)I", null, null);
                final MethodVisitor receiveEnergyMethodInjector = new receiveEnergyMethodInjector(v, receiveEnergyVisitor);
                receiveEnergyMethodInjector.visitCode();

                final MethodVisitor extractEnergyVisitor = super.visitMethod(Opcodes.ACC_PUBLIC, "extractEnergy", "(IZ)I", null, null);
                final MethodVisitor extractEnergyMethodInjector = new extractEnergyMethodInjector(v, extractEnergyVisitor);
                extractEnergyMethodInjector.visitCode();

                final MethodVisitor getEnergyStoredVisitor = super.visitMethod(Opcodes.ACC_PUBLIC, "getEnergyStored", "()I", null, null);
                final MethodVisitor getEnergyStoredMethodInjector = new getEnergyStoredMethodInjector(v, getEnergyStoredVisitor);
                getEnergyStoredMethodInjector.visitCode();

                final MethodVisitor getMaxEnergyStoredVisitor = super.visitMethod(Opcodes.ACC_PUBLIC, "getMaxEnergyStored", "()I", null, null);
                final MethodVisitor getMaxEnergyStoredMethodInjector = new getMaxEnergyStoredMethodInjector(v, getMaxEnergyStoredVisitor);
                getMaxEnergyStoredMethodInjector.visitCode();

                final MethodVisitor canExtractEnergyVisitor = super.visitMethod(Opcodes.ACC_PUBLIC, "canExtract", "()Z", null, null);
                final MethodVisitor canExtractMethodInjector = new canExtractMethodInjector(v, canExtractEnergyVisitor);
                canExtractMethodInjector.visitCode();

                final MethodVisitor canReceiveVisitor = super.visitMethod(Opcodes.ACC_PUBLIC, "canReceive", "()Z", null, null);
                final MethodVisitor canReceiveMethodInjector = new canReceiveMethodInjector(v, canReceiveVisitor);
                canReceiveMethodInjector.visitCode();

                super.visitEnd();
            }
        };
    }

    private static final class getEnergyStoredMethodInjector extends MethodVisitor {
        private getEnergyStoredMethodInjector(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(109, l0);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "buildcraft/api/mj/MjBattery", "microJoules", "J");
            super.visitInsn(Opcodes.L2D);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "buildcraft/api/mj/MjBattery", "fromMJ", "D");
            super.visitInsn(Opcodes.DMUL);
            super.visitInsn(Opcodes.D2I);
            super.visitInsn(Opcodes.IRETURN);
            Label l1 = new Label();
            super.visitLabel(l1);
            super.visitLocalVariable("this", "Lbuildcraft/api/mj/MjBattery;", null, l0, l1, 0);
            super.visitMaxs(4, 1);
            super.visitEnd();
        }
    }

    private static final class getMaxEnergyStoredMethodInjector extends MethodVisitor {
        private getMaxEnergyStoredMethodInjector(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(114, l0);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "buildcraft/api/mj/MjBattery", "capacity", "J");
            super.visitInsn(Opcodes.L2D);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "buildcraft/api/mj/MjBattery", "fromMJ", "D");
            super.visitInsn(Opcodes.DMUL);
            super.visitInsn(Opcodes.D2I);
            super.visitInsn(Opcodes.IRETURN);
            Label l1 = new Label();
            super.visitLabel(l1);
            super.visitLocalVariable("this", "Lbuildcraft/api/mj/MjBattery;", null, l0, l1, 0);
            super.visitMaxs(4, 1);
            super.visitEnd();
        }
    }

    private static final class canExtractMethodInjector extends MethodVisitor {
        private canExtractMethodInjector(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(119, l0);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "buildcraft/api/mj/MjBattery", "microJoules", "J");
            super.visitInsn(Opcodes.LCONST_0);
            super.visitInsn(Opcodes.LCMP);
            Label l1 = new Label();
            super.visitJumpInsn(Opcodes.IFLE, l1);
            super.visitInsn(Opcodes.ICONST_1);
            Label l2 = new Label();
            super.visitJumpInsn(Opcodes.GOTO, l2);
            super.visitLabel(l1);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitInsn(Opcodes.ICONST_0);
            super.visitLabel(l2);
            super.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{Opcodes.INTEGER});
            super.visitInsn(Opcodes.IRETURN);
            Label l3 = new Label();
            super.visitLabel(l3);
            super.visitLocalVariable("this", "Lbuildcraft/api/mj/MjBattery;", null, l0, l3, 0);
            super.visitMaxs(4, 1);
            super.visitEnd();
        }
    }

    private static final class canReceiveMethodInjector extends MethodVisitor {
        private canReceiveMethodInjector(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(124, l0);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "buildcraft/api/mj/MjBattery", "microJoules", "J");
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "buildcraft/api/mj/MjBattery", "capacity", "J");
            super.visitInsn(Opcodes.LCMP);
            Label l1 = new Label();
            super.visitJumpInsn(Opcodes.IFGE, l1);
            super.visitInsn(Opcodes.ICONST_1);
            Label l2 = new Label();
            super.visitJumpInsn(Opcodes.GOTO, l2);
            super.visitLabel(l1);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitInsn(Opcodes.ICONST_0);
            super.visitLabel(l2);
            super.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{Opcodes.INTEGER});
            super.visitInsn(Opcodes.IRETURN);
            Label l3 = new Label();
            super.visitLabel(l3);
            super.visitLocalVariable("this", "Lbuildcraft/api/mj/MjBattery;", null, l0, l3, 0);
            super.visitMaxs(4, 1);
            super.visitEnd();
        }
    }

    private static final class extractEnergyMethodInjector extends MethodVisitor {
        private extractEnergyMethodInjector(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }


        @Override
        public void visitCode() {
            super.visitCode();
            Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(98, l0);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "buildcraft/api/mj/MjBattery", "canExtract", "()Z", false);
            Label l1 = new Label();
            super.visitJumpInsn(Opcodes.IFNE, l1);
            Label l2 = new Label();
            super.visitLabel(l2);
            super.visitLineNumber(99, l2);
            super.visitInsn(Opcodes.ICONST_0);
            super.visitInsn(Opcodes.IRETURN);
            super.visitLabel(l1);
            super.visitLineNumber(101, l1);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "buildcraft/api/mj/MjBattery", "microJoules", "J");
            super.visitInsn(Opcodes.L2D);
            super.visitIntInsn(Opcodes.SIPUSH, 2048);
            super.visitVarInsn(Opcodes.ILOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "min", "(II)I", false);
            super.visitInsn(Opcodes.I2D);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "buildcraft/api/mj/MjBattery", "toMJ", "D");
            super.visitInsn(Opcodes.DMUL);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "min", "(DD)D", false);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "round", "(D)J", false);
            super.visitVarInsn(Opcodes.LSTORE, 3);
            Label l3 = new Label();
            super.visitLabel(l3);
            super.visitLineNumber(102, l3);
            super.visitVarInsn(Opcodes.ILOAD, 2);
            Label l4 = new Label();
            super.visitJumpInsn(Opcodes.IFNE, l4);
            Label l5 = new Label();
            super.visitLabel(l5);
            super.visitLineNumber(103, l5);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitInsn(Opcodes.DUP);
            super.visitFieldInsn(Opcodes.GETFIELD, "buildcraft/api/mj/MjBattery", "microJoules", "J");
            super.visitVarInsn(Opcodes.LLOAD, 3);
            super.visitInsn(Opcodes.LSUB);
            super.visitFieldInsn(Opcodes.PUTFIELD, "buildcraft/api/mj/MjBattery", "microJoules", "J");
            super.visitLabel(l4);
            super.visitLineNumber(104, l4);
            super.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.LONG}, 0, null);
            super.visitVarInsn(Opcodes.LLOAD, 3);
            super.visitInsn(Opcodes.L2D);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "buildcraft/api/mj/MjBattery", "fromMJ", "D");
            super.visitInsn(Opcodes.DMUL);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "round", "(D)J", false);
            super.visitInsn(Opcodes.L2I);
            super.visitInsn(Opcodes.IRETURN);
            Label l6 = new Label();
            super.visitLabel(l6);
            super.visitLocalVariable("this", "Lbuildcraft/api/mj/MjBattery;", null, l0, l6, 0);
            super.visitLocalVariable("maxExtract", "I", null, l0, l6, 1);
            super.visitLocalVariable("simulate", "Z", null, l0, l6, 2);
            super.visitLocalVariable("energyExtracted", "J", null, l3, l6, 3);
            super.visitMaxs(6, 5);
            super.visitEnd();
        }
    }

    private static final class receiveEnergyMethodInjector extends MethodVisitor {
        private receiveEnergyMethodInjector(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(87, l0);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "buildcraft/api/mj/MjBattery", "canReceive", "()Z", false);
            Label l1 = new Label();
            super.visitJumpInsn(Opcodes.IFNE, l1);
            Label l2 = new Label();
            super.visitLabel(l2);
            super.visitLineNumber(88, l2);
            super.visitInsn(Opcodes.ICONST_0);
            super.visitInsn(Opcodes.IRETURN);
            super.visitLabel(l1);
            super.visitLineNumber(90, l1);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "buildcraft/api/mj/MjBattery", "capacity", "J");
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "buildcraft/api/mj/MjBattery", "microJoules", "J");
            super.visitInsn(Opcodes.LSUB);
            super.visitInsn(Opcodes.L2D);
            super.visitIntInsn(Opcodes.SIPUSH, 2048);
            super.visitVarInsn(Opcodes.ILOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "min", "(II)I", false);
            super.visitInsn(Opcodes.I2D);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "buildcraft/api/mj/MjBattery", "toMJ", "D");
            super.visitInsn(Opcodes.DMUL);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "min", "(DD)D", false);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "round", "(D)J", false);
            super.visitVarInsn(Opcodes.LSTORE, 3);
            Label l3 = new Label();
            super.visitLabel(l3);
            super.visitLineNumber(91, l3);
            super.visitVarInsn(Opcodes.ILOAD, 2);
            Label l4 = new Label();
            super.visitJumpInsn(Opcodes.IFNE, l4);
            Label l5 = new Label();
            super.visitLabel(l5);
            super.visitLineNumber(92, l5);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitInsn(Opcodes.DUP);
            super.visitFieldInsn(Opcodes.GETFIELD, "buildcraft/api/mj/MjBattery", "microJoules", "J");
            super.visitVarInsn(Opcodes.LLOAD, 3);
            super.visitInsn(Opcodes.LADD);
            super.visitFieldInsn(Opcodes.PUTFIELD, "buildcraft/api/mj/MjBattery", "microJoules", "J");
            super.visitLabel(l4);
            super.visitLineNumber(93, l4);
            super.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.LONG}, 0, null);
            super.visitVarInsn(Opcodes.LLOAD, 3);
            super.visitInsn(Opcodes.L2D);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "buildcraft/api/mj/MjBattery", "fromMJ", "D");
            super.visitInsn(Opcodes.DMUL);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "round", "(D)J", false);
            super.visitInsn(Opcodes.L2I);
            super.visitInsn(Opcodes.IRETURN);
            Label l6 = new Label();
            super.visitLabel(l6);
            super.visitLocalVariable("this", "Lbuildcraft/api/mj/MjBattery;", null, l0, l6, 0);
            super.visitLocalVariable("maxReceive", "I", null, l0, l6, 1);
            super.visitLocalVariable("simulate", "Z", null, l0, l6, 2);
            super.visitLocalVariable("energyReceived", "J", null, l3, l6, 3);
            super.visitMaxs(6, 5);
            super.visitEnd();
        }
    }
}
