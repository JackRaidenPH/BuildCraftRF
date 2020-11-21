package by.jackraidenph.bcrf.asm.transformer;

import net.thesilkminer.mc.fermion.asm.api.LaunchPlugin;
import net.thesilkminer.mc.fermion.asm.api.descriptor.ClassDescriptor;
import net.thesilkminer.mc.fermion.asm.api.transformer.TransformerData;
import net.thesilkminer.mc.fermion.asm.prefab.AbstractTransformer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public class MjBatteryReceiverTransformer extends AbstractTransformer {

    public MjBatteryReceiverTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("mj_battery_receiver_transformer")
                        .setDescription("Patches MjBatteryReceiver to add MjBattery getter")
                        .build(),
                ClassDescriptor.of("buildcraft.lib.mj.MjBatteryReceiver")
        );
        getBatteryMethodInjector.class.toString();
    }

    @Nonnull
    @Override
    public BiFunction<Integer, ClassVisitor, ClassVisitor> getClassVisitorCreator() {
        return (v, cw) -> new ClassVisitor(v, cw) {
            @Override
            public void visitEnd() {

                final MethodVisitor getBatteryVisitor = super.visitMethod(Opcodes.ACC_PUBLIC, "getBattery", "()Lbuildcraft/api/mj/MjBattery;", null, null);
                final MethodVisitor getBatteryMethodInjector = new getBatteryMethodInjector(v, getBatteryVisitor);
                getBatteryMethodInjector.visitCode();

                super.visitEnd();
            }
        };
    }

    private static final class getBatteryMethodInjector extends MethodVisitor {
        private getBatteryMethodInjector(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(19, l0);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "buildcraft/lib/mj/MjBatteryReceiver", "battery", "Lbuildcraft/api/mj/MjBattery;");
            super.visitInsn(Opcodes.ARETURN);
            Label l1 = new Label();
            super.visitLabel(l1);
            super.visitLocalVariable("this", "Lbuildcraft/lib/mj/MjBatteryReceiver;", null, l0, l1, 0);
            super.visitMaxs(1, 1);
            super.visitEnd();
        }
    }
}
