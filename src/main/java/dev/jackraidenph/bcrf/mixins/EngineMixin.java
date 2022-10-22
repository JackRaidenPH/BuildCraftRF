package dev.jackraidenph.bcrf.mixins;

import buildcraft.api.mj.IMjReceiver;
import buildcraft.lib.engine.TileEngineBase_BC8;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEngineBase_BC8.class)
public abstract class EngineMixin {

    private float toMJ = 1_000_00L / 15F;
    private float fromMJ = 15F / 1_000_00L;

    @Shadow
    public abstract TileEngineBase_BC8.ITileBuffer getTileBuffer(EnumFacing side);

    @Shadow
    protected EnumFacing currentDirection;

    @Shadow
    public abstract EnumFacing getCurrentFacing();

    @Shadow
    public abstract long getEnergyStored();

    @Shadow public abstract long maxPowerExtracted();

    @Shadow protected abstract long getPowerToExtract(boolean doExtract);

    @Shadow public abstract long extractPower(long min, long max, boolean doExtract);

    @Inject(at = @At("HEAD"), method = "getPowerToExtract", remap = false, cancellable = true)
    public void getPowerToExtractInjector(boolean doExtract, CallbackInfoReturnable<Long> cir) {
        TileEntity tile = getTileBuffer(currentDirection).getTile();
        if (tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, getCurrentFacing().getOpposite())) {
            IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, getCurrentFacing().getOpposite());
            if(storage != null) {
                long toExtract = (long) Math.min(maxPowerExtracted(), Math.min(getEnergyStored(), (storage.getMaxEnergyStored() - storage.getEnergyStored()) * toMJ));
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
        TileEntity thisInstance = ((TileEntity) (Object) this);
        TileEntity tile = thisInstance.getWorld().getTileEntity(thisInstance.getPos().offset(dir));

        if (tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, dir.getOpposite()))
            cir.setReturnValue(true);
    }
}
