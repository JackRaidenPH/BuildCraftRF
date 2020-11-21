package by.jackraidenph.bcrf;

import buildcraft.api.mj.MjAPI;
import buildcraft.api.mj.MjBattery;
import buildcraft.lib.tile.TileBC_Neptune;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

@Mod(modid = BuildCraftRF.MODID, name = BuildCraftRF.NAME, version = BuildCraftRF.VERSION, dependencies = "required-after:buildcraftcore@[7.99.12,);required-after:fermion@[1.0.2,);")
public class BuildCraftRF {
    public static final String MODID = "bcrf";
    public static final String NAME = "BuildCraftRF";
    public static final String VERSION = "rev.1.5";
    public static final ResourceLocation CAPABILITY_KEY = new ResourceLocation(MODID, "ForgeEnergyCapability");

    public static final ResourceLocation FILLER_RECIPE_KEY = new ResourceLocation(MODID, "bc_filler");
    public static final ResourceLocation BUILDER_RECIPE_KEY = new ResourceLocation(MODID, "bc_builder");
    public static final ResourceLocation LIBRARY_RECIPE_KEY = new ResourceLocation(MODID, "bc_library");
    public static final ResourceLocation REPLACER_RECIPE_KEY = new ResourceLocation(MODID, "bc_replacer");
    public static final ResourceLocation ARCHITECT_RECIPE_KEY = new ResourceLocation(MODID, "bc_architect");

    public static Configuration CONFIG;
    public static Logger LOGGER;
    private double ratio;
    private boolean areExtraRecipesEnabled;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        CONFIG = new Configuration(event.getSuggestedConfigurationFile());
        CONFIG.load();
        ratio = CONFIG.getFloat("ratio", "general", 15.0f, 1.0f, Float.MAX_VALUE, "Sets the conversion ratio for BuildCraft machines.");
        CONFIG.save();
        LOGGER = event.getModLog();
    }

    private void trySetValues(TileEntity te) {
        if (te instanceof TileBC_Neptune) {
            for (Field f : te.getClass().getDeclaredFields()) {
                if (f.getType() == MjBattery.class) {
                    try {

                        f.setAccessible(true);
                        Class batteryClazz = f.get(te).getClass();
                        Field[] fieldsList = batteryClazz.getDeclaredFields();

                        for (Field x : fieldsList) {
                            if (x.getName().trim().equals("toMJ")) {
                                x.setAccessible(true);
                                x.set(f.get(te), MjAPI.MJ / ratio);
                            }
                            if (x.getName().trim().equals("fromMJ")) {
                                x.setAccessible(true);
                                x.set(f.get(te), ratio / MjAPI.MJ);
                            }
                        }

                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(PlayerEvent.PlayerLoggedInEvent e) {
        for (TileEntity te : e.player.world.loadedTileEntityList)
            trySetValues(te);
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent e) {
        if (e.getPlacedBlock().getBlock().hasTileEntity(e.getPlacedBlock())) {
            TileEntity te = e.getWorld().getTileEntity(e.getPos());
            trySetValues(te);
        }
    }

    @SubscribeEvent
    public void onCapability(AttachCapabilitiesEvent e) {
        if (e.getObject() instanceof TileBC_Neptune & !(e.getCapabilities().containsKey(CAPABILITY_KEY))) {
            for (Field f : e.getObject().getClass().getDeclaredFields()) {
                if (f.getType() == MjBattery.class) {
                    e.addCapability(CAPABILITY_KEY, new InsertionCapabilityProvider(f, (TileEntity) e.getObject()));
                    break;
                }
            }
        }
    }
}
