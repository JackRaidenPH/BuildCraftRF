package dev.jackraidenph.bcrf;

import buildcraft.api.mj.MjAPI;
import buildcraft.api.mj.MjBattery;
import buildcraft.lib.engine.TileEngineBase_BC8;
import buildcraft.lib.tile.TileBC_Neptune;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;

@Mod(modid = BuildCraftRF.MODID, name = BuildCraftRF.NAME, version = BuildCraftRF.VERSION, dependencies = "required-after:buildcraftcore@[7.99.12,);required-after:mixinbooter@[5.0,);")
public class BuildCraftRF {
    public static final String MODID = "bcrf";
    public static final String NAME = "BuildCraftRF";
    public static final String VERSION = "2.1.1";
    public static final ResourceLocation CAPABILITY_KEY = new ResourceLocation(MODID, "ForgeEnergyCapability");

    public static Configuration CONFIG;
    public static Logger LOGGER_MOD;
    private float ratio;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        CONFIG = new Configuration(new File(event.getSuggestedConfigurationFile().getAbsolutePath().replace(MODID, "buildcraft-rf")));
        CONFIG.load();
        ratio = CONFIG.getFloat("ratio", "general", 15F, 1F, Float.MAX_VALUE, "Sets the conversion ratio for BuildCraft machines.");
        CONFIG.save();
        LOGGER_MOD = event.getModLog();
    }

    private void trySetValues(TileEntity te) {
        try {
            if (te instanceof TileBC_Neptune) {
                Field toSet;

                if (te instanceof TileEngineBase_BC8) {
                    toSet = TileEngineBase_BC8.class.getDeclaredField("toMJ");
                    toSet.setAccessible(true);
                    toSet.set(te, MjAPI.MJ / ratio);

                    toSet = TileEngineBase_BC8.class.getDeclaredField("fromMJ");
                    toSet.setAccessible(true);
                    toSet.set(te, ratio / MjAPI.MJ);

                    return;
                }

                Field f = findField(te.getClass(), MjBattery.class);
                if (f != null) {

                    f.setAccessible(true);

                    toSet = MjBattery.class.getDeclaredField("toMJ");
                    toSet.setAccessible(true);
                    toSet.set(f.get(te), MjAPI.MJ / ratio);

                    toSet = MjBattery.class.getDeclaredField("fromMJ");
                    toSet.setAccessible(true);
                    toSet.set(f.get(te), ratio / MjAPI.MJ);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent e) {
        if (e.getPlacedBlock().getBlock().hasTileEntity(e.getPlacedBlock())) {
            TileEntity te = e.getWorld().getTileEntity(e.getPos());
            trySetValues(te);
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load e) {
        e.getChunk().getTileEntityMap().forEach((x, y) -> trySetValues(y));
    }

    private Field findField(Class obj, Class clazz) {
        for (Field f : obj.getDeclaredFields())
            if (f.getType() == clazz)
                return f;

        if (obj.getSuperclass() != null)
            return findField(obj.getSuperclass(), clazz);
        else return null;
    }

    @SubscribeEvent
    public void onCapability(AttachCapabilitiesEvent e) {
        if (e.getObject() instanceof TileBC_Neptune & !(e.getCapabilities().containsKey(CAPABILITY_KEY))) {
            Field f = findField(e.getObject().getClass(), MjBattery.class);
            if (f != null) {
                e.addCapability(CAPABILITY_KEY, new InsertionCapabilityProvider(f, (TileEntity) e.getObject()));
            }
        }
    }
}
