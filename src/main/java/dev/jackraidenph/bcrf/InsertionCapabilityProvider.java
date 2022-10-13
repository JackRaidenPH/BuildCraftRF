package dev.jackraidenph.bcrf;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class InsertionCapabilityProvider implements ICapabilityProvider {

    private Field f;
    private TileEntity te;

    protected InsertionCapabilityProvider(Field batteryField, TileEntity te) {
        f = batteryField;
        this.te = te;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return this.getCapability(capability, facing) != null;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            try {
                f.setAccessible(true);
                return CapabilityEnergy.ENERGY.cast((IEnergyStorage) f.get(te));
            } catch (IllegalAccessException ex) {
                BuildCraftRF.LOGGER_MOD.info("Failed to attach capability{} to object{}", this, te.getClass().getSimpleName());
                ex.printStackTrace();
            }
        }
        return null;
    }
}

