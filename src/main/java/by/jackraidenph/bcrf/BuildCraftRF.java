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
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;

@Mod(modid = BuildCraftRF.MODID, name = BuildCraftRF.NAME, version = BuildCraftRF.VERSION, dependencies = "required-after:buildcraftcore@[7.99.12,);required-after:fermion@[1.0.2,);")
public class BuildCraftRF {
    public static final String MODID = "bcrf";
    public static final String NAME = "BuildCraftRF";
    public static final String VERSION = "1.5.4";
    public static final ResourceLocation CAPABILITY_KEY = new ResourceLocation(MODID, "ForgeEnergyCapability");

    public static Configuration CONFIG;
    public static Logger LOGGER_MOD;
    private double ratio;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        CONFIG = new Configuration(new File(event.getSuggestedConfigurationFile().getAbsolutePath().replace(MODID, "buildcraft-rf")));
        CONFIG.load();
        ratio = CONFIG.getFloat("ratio", "general", 15.0f, 1.0f, Float.MAX_VALUE, "Sets the conversion ratio for BuildCraft machines.");
        CONFIG.save();
        LOGGER_MOD = event.getModLog();
    }

    private void trySetValues(TileEntity te) {
        if (te instanceof TileBC_Neptune) {
            Field f = findField(te.getClass(), MjBattery.class);
            if (f != null) {
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
