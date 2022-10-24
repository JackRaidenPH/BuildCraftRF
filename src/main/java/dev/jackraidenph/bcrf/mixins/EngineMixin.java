package dev.jackraidenph.bcrf.mixins;

import buildcraft.api.mj.IMjReceiver;
import buildcraft.lib.engine.TileEngineBase_BC8;
import buildcraft.lib.tile.TileBC_Neptune;
import dev.jackraidenph.bcrf.BuildCraftRF;
import dev.jackraidenph.bcrf.IRotationProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(TileEngineBase_BC8.class)
public abstract class EngineMixin extends TileBC_Neptune implements IEnergyStorage, IRotationProvider {

    private float toMJ = 1_000_00L / 15F;
    private float fromMJ = 15F / 1_000_00L;

    @Shadow(remap = false)
    public abstract TileEngineBase_BC8.ITileBuffer getTileBuffer(EnumFacing side);

    @Shadow(remap = false)
    protected EnumFacing currentDirection;

    @Shadow(remap = false)
    public abstract EnumFacing getCurrentFacing();

    @Shadow(remap = false)
    public abstract long maxPowerExtracted();

    @Shadow(remap = false)
    protected abstract long getPowerToExtract(boolean doExtract);

    @Shadow(remap = false)
    public abstract long extractPower(long min, long max, boolean doExtract);

    @Shadow(remap = false)
    protected long power;

    @Shadow(remap = false)
    public abstract long getMaxPower();

    @Shadow(remap = false)
    protected abstract boolean isFacingReceiver(EnumFacing dir);

    @Inject(at = @At("HEAD"), method = "getCapability", remap = false, cancellable = true)
    public void getCapabilityInjector(@Nonnull Capability<IEnergyStorage> capability, EnumFacing facing, CallbackInfoReturnable<Object> cir) {
        if (capability == CapabilityEnergy.ENERGY)
            cir.setReturnValue(super.getCapability(capability, facing));
    }

    @Inject(at = @At("HEAD"), method = "getPowerToExtract", remap = false, cancellable = true)
    public void getPowerToExtractInjector(boolean doExtract, CallbackInfoReturnable<Long> cir) {
        TileEntity tile = getTileBuffer(currentDirection).getTile();
        if (tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, getCurrentFacing().getOpposite())) {
            IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, getCurrentFacing().getOpposite());
            if (storage != null) {
                long toExtract = (long) Math.min(maxPowerExtracted(), Math.min(power, (storage.getMaxEnergyStored() - storage.getEnergyStored()) * toMJ));
                cir.setReturnValue(extractPower(0L, toExtract, doExtract));
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "sendPower", remap = false, cancellable = true)
    public void sendPowerInjector(CallbackInfo ci) {
        TileEntity tile = getTileBuffer(currentDirection).getTile();
        if (tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, getCurrentFacing().getOpposite())) {
            IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, getCurrentFacing().getOpposite());
            long extracted = getPowerToExtract(true);
            if (storage != null && extracted > 0L) {
                long excess = (long) (storage.receiveEnergy((int) (extracted * fromMJ), false) * toMJ);
                extractPower(extracted - excess, extracted - excess, true);
                ci.cancel();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "isFacingReceiver", remap = false, cancellable = true)
    public void isFacingReceiverInjector(EnumFacing dir, CallbackInfoReturnable<Boolean> cir) {
        TileEntity tile = this.getWorld().getTileEntity(this.getPos().offset(dir));

        if (tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, dir.getOpposite()))
            cir.setReturnValue(true);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        long energyExtracted = Math.round(Math.min(power, Math.min(2048, maxExtract) * toMJ));
        if (!simulate)
            power -= energyExtracted;
        return Math.round(energyExtracted * fromMJ);
    }

    @Override
    public int getEnergyStored() {
        return (int) (power * fromMJ);
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) (getMaxPower() * fromMJ);
    }

    @Override
    public boolean canExtract() {
        return power > 0;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    @Override
    public void rotateToCapGetter(EnumFacing facing) {
        if(currentDirection != null && isFacingReceiver(currentDirection))
            return;

        currentDirection = facing;
        sendNetworkUpdate(NET_RENDER_DATA);
        redrawBlock();
    }
}
