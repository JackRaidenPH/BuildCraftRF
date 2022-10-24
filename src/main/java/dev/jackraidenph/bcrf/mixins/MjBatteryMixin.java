package dev.jackraidenph.bcrf.mixins;

import buildcraft.api.mj.MjBattery;
import net.minecraftforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MjBattery.class)
public class MjBatteryMixin implements IEnergyStorage {

    @Shadow(remap = false)
    private long microJoules;

    @Shadow(remap = false)
    @Final
    private long capacity;

    private float toMJ = 1_000_00L / 15F;
    private float fromMJ = 15F / 1_000_00L;

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        long energyReceived = Math.round(Math.min(capacity - microJoules, Math.min(2048, maxReceive) * toMJ));
        if (!simulate)
            microJoules += energyReceived;
        return Math.round(energyReceived * fromMJ);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        long energyExtracted = Math.round(Math.min(microJoules, Math.min(2048, maxExtract) * toMJ));
        if (!simulate)
            microJoules -= energyExtracted;
        return Math.round(energyExtracted * fromMJ);
    }

    @Override
    public int getEnergyStored() {
        return (int) (this.microJoules * fromMJ);
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) (capacity * fromMJ);
    }

    @Override
    public boolean canExtract() {
        return microJoules > 0;
    }

    @Override
    public boolean canReceive() {
        return microJoules < capacity;
    }
}
