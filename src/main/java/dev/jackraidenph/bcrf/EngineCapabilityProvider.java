package dev.jackraidenph.bcrf;

import buildcraft.lib.engine.TileEngineBase_BC8;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class EngineCapabilityProvider implements ICapabilityProvider {

    private TileEngineBase_BC8 te;

    protected EngineCapabilityProvider(TileEngineBase_BC8 te) {
        this.te = te;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return this.getCapability(capability, facing) != null;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (te.getCurrentFacing() == null)
            return null;

        if (capability == CapabilityEnergy.ENERGY && (facing == null || facing.equals(te.getCurrentFacing())))
            return CapabilityEnergy.ENERGY.cast((IEnergyStorage) te);

        return null;
    }
}

