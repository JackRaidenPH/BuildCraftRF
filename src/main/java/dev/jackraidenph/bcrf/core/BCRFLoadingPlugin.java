package dev.jackraidenph.bcrf.core;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.SortingIndex(-8000)
@IFMLLoadingPlugin.Name("BuildCraftRF Mixin Loading Plugin")
public class BCRFLoadingPlugin implements IFMLLoadingPlugin {
    public static Logger log = LogManager.getLogger("ErebusFix Core");

    public BCRFLoadingPlugin() {
        log.info("Attempting to bootstrap Mixins and plugins.");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.loader.json");
    }

    @Override
    public String[] getASMTransformerClass() { return new String[0]; }

    @Override
    public String getModContainerClass() { return null; }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() { return null; }
}