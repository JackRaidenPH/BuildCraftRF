package dev.jackraidenph.bcrf.asm;

import dev.jackraidenph.bcrf.asm.transformer.MjBatteryReceiverTransformer;
import dev.jackraidenph.bcrf.asm.transformer.MjBatteryTransformer;
import com.google.common.collect.ImmutableSet;
import net.thesilkminer.mc.fermion.asm.api.PluginMetadata;
import net.thesilkminer.mc.fermion.asm.prefab.AbstractLaunchPlugin;

import javax.annotation.Nonnull;
import java.util.Set;

public class BuildCraftPlugin extends AbstractLaunchPlugin {

    public BuildCraftPlugin() {
        super("bcrf.asm");
        this.registerTransformer(new MjBatteryReceiverTransformer(this));
        this.registerTransformer(new MjBatteryTransformer(this));
    }

    @Override
    protected void populateMetadata(@Nonnull PluginMetadata.Builder builder) {
        builder.setName("BuildCraft ASM")
                .addAuthor("JackRaidenPH")
                .setCredits("JackRaidenPH, BuildCraft DEV Team")
                .setDescription("Modifies BuildCraft classes to bring back ForgeEnergy support")
                .setVersion("1.0.0")
                .build();
    }

    @Nonnull
    @Override
    public Set<String> getRootPackages() {
        return ImmutableSet.of("by.jackraidenph.bcrf.asm");
    }

}
