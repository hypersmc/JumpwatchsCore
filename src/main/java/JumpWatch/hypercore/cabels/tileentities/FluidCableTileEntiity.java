package JumpWatch.hypercore.cabels.tileentities;

import JumpWatch.hypercore.ModSettings;
import JumpWatch.hypercore.utils.EnumCableType;
import JumpWatch.hypercore.utils.helplogger;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FluidCableTileEntiity extends TileEntity implements ITickable, IFluidHandler {
    private EnumCableType cableTypes;
    protected FluidTank tank = new FluidTank(ModSettings.fluidCableProperties.FluidAmount);
    private int max = 1000;
    private List sidesReceivedFrom = new ArrayList();
    private List sides = new IntArrayList();
    public boolean covered;
    public String fluid = " ";
    public List rendersides = new IntArrayList();
    public List boxes = new ArrayList();
    private int tempF;
    private static Map<EnumCableType, Integer> losses = new HashMap<EnumCableType, Integer>();
    private static Map<EnumCableType, Integer> capacities = new HashMap<EnumCableType, Integer>();

    static {
        losses.put(EnumCableType.C1, 1);
        capacities.put(EnumCableType.C1, 32);
        losses.put(EnumCableType.C2, 1);
        capacities.put(EnumCableType.C2, 64);
        losses.put(EnumCableType.C3, 1);
        capacities.put(EnumCableType.C3, 128);
        losses.put(EnumCableType.C4, 1);
        capacities.put(EnumCableType.C4, 256);
        losses.put(EnumCableType.C5, 1);
        capacities.put(EnumCableType.C5, 512);
        losses.put(EnumCableType.C6, 1);
        capacities.put(EnumCableType.C6, 1024);
        losses.put(EnumCableType.C7, 1);
        capacities.put(EnumCableType.C7, 2048);
        losses.put(EnumCableType.C8, 1);
        capacities.put(EnumCableType.C8, 4096);
        losses.put(EnumCableType.C9, 1);
        capacities.put(EnumCableType.C9, 8192);
        losses.put(EnumCableType.C10, 0);
        capacities.put(EnumCableType.C10, 16384);
        losses.put(EnumCableType.C11, 0);
        capacities.put(EnumCableType.C11, 32768);
        losses.put(EnumCableType.C12, 0);
        capacities.put(EnumCableType.C12, 65536);
        losses.put(EnumCableType.C13, 0);
        capacities.put(EnumCableType.C13, 131072);
        losses.put(EnumCableType.C14, 0);
        capacities.put(EnumCableType.C14, 262144);
        losses.put(EnumCableType.C15, 0);
        capacities.put(EnumCableType.C15, 524288);
        losses.put(EnumCableType.C16, 0);
        capacities.put(EnumCableType.C16, 1048576);
        losses.put(EnumCableType.C17, 0);
        capacities.put(EnumCableType.C17, 2097152);
        losses.put(EnumCableType.C18, 0);
        capacities.put(EnumCableType.C18, 4194304);
        losses.put(EnumCableType.C19, 0);
        capacities.put(EnumCableType.C19, 8388608);
        losses.put(EnumCableType.C20, 0);
        capacities.put(EnumCableType.C20, 16777216);
    }

    public static AxisAlignedBB[] uncoveredBoxes = {
            new AxisAlignedBB(0.3125, 0, 0.3125, 0.6875, 0.3125, 0.6875),
            new AxisAlignedBB(0.3125, 0.6875, 0.3125, 0.6875, 1, 0.6875),
            new AxisAlignedBB(0.3125, 0.3125, 0, 0.6875, 0.6875, 0.3125),
            new AxisAlignedBB(0.3125, 0.3125, 0.6875, 0.6875, 0.6875, 1),
            new AxisAlignedBB(0, 0.3125, 0.3125, 0.3125, 0.6875, 0.6875),
            new AxisAlignedBB(0.6875, 0.3125, 0.3125, 1, 0.6875, 0.6875)
    };


    public static String[] sideStates = {"no restrictions", "input only", "output only", "disabled"};

    public void init(EnumCableType type, boolean covered) {
        this.covered = covered;
        this.cableTypes = type;
        if (sides.size() != 6) {
            for (int i = 0; i < 6; i++) {
                sides.add(0);
            }
        }

    }

    private int fill(EnumFacing from, FluidStack fluid, boolean simulate) {
        if (from == null) {
            int fluidReceived = tank.fillInternal(fluid, simulate);
            if (this.tank.getFluidAmount() > this.max) {
                //
            }
            tempF += fluidReceived;
            if (tempF > max)
                tempF = max;
            return fluidReceived;
        }
        if ((Integer) sides.get(from.getIndex()) < 2) {
            int fluidReceived = tank.fillInternal(fluid, simulate);
            if (this.tank.getFluidAmount() > this.max) {
                //
            }
            if (fluidReceived > 0) {
                sidesReceivedFrom.add(from);
            }
            tempF += fluidReceived;
            if (tempF > max)
                tempF = max;
            return fluidReceived;
        } else {
            return 0;
        }
    }

    private FluidStack drain(EnumFacing from, FluidStack fluid, boolean simulate) {
        if (from == null) {
            return tank.drainInternal(fluid, simulate);
        }
        if ((Integer) sides.get(from.getIndex()) == 0 || (Integer) sides.get(from.getIndex()) == 2) {
            return tank.drainInternal(fluid, simulate);
        }
        return tank.getFluid();
    }

    public void incrementSide(int side, EntityPlayer player, World world) {
        sides.set(side, (Integer) sides.get(side) + 1);
        if ((Integer) sides.get(side) > 3) {
            sides.set(side, 0);
        }
        if (world.isRemote) {
            player.sendMessage(new TextComponentString(EnumFacing.getFront(side) + " side set to " + sideStates[(Integer) sides.get(side)]));
        }
    }


    public int getFluidAmount(EnumFacing from) {
        return tank.getFluidAmount();
    }

    @Override
    public void update() {
        List sidesFluidTo = new ArrayList();
        if (sides.size() != 6) {
            for (int i = 0; i < 6; i++) {
                sides.add(0);
            }
        }
        float fluidSplit = 0;
        rendersides.clear();
        for (int i = 0; i < 6; i++) {
            EnumFacing side = EnumFacing.getFront(i);
            Vec3i offset = new Vec3i(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
            if (!(sidesReceivedFrom.contains(side))) {
                if (this.world.getTileEntity(this.getPos().add(offset)) != null) {
                    TileEntity tileEntity = this.world.getTileEntity(this.getPos().add(offset));
                    if ((tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()))) {
                        helplogger.info("We got this far");
                        //FluidStack stack;
                        IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
                        if (tank.getFluidAmount() < 1) {
                            FluidUtil.tryFluidTransfer(tank, handler, ModSettings.fluidCableProperties.FluidTransfer, true);
                        }else {
                            FluidUtil.tryFluidTransfer(handler, tank, ModSettings.fluidCableProperties.FluidTransfer, true);

                        }
                        //tank.drain(handler.fill(tank.drain(20, false), true), true);
                        //helplogger.info("Draining shuld have been doine now!");
                        //tank.drain(handler.fill(tank.drain(20, false), true), true);

                    }
                }
            }
        }

        tempF = 0;
        for (int i = 0; i < 6; i++) {
            EnumFacing side = EnumFacing.getFront(i);
            Vec3i offset = new Vec3i(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
            if (!(sidesReceivedFrom.contains(side))) {
                if (this.world.getTileEntity(this.getPos().add(offset)) != null) {
                    TileEntity tileEntity = this.world.getTileEntity(this.getPos().add(offset));
                    if ((tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()))) {
                        fluidSplit++;
                        this.rendersides.add(this.sides.get(i));

                    } else {
                        this.rendersides.add(3);
                    }
                } else {
                    this.rendersides.add(3);
                }
            } else {
                this.rendersides.add(this.sides.get(i));
            }
        }

        if (this.tank.getFluidAmount() > 0) {
            if (fluidSplit > 0) {
                int fluidtotrabsnut = (int) Math.floor(this.tank.getFluidAmount() / fluidSplit);
                for (int i = 0; i < 6; i++) {
                    EnumFacing side = EnumFacing.getFront(i);
                    Vec3i offset = new Vec3i(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
                    if (!(sidesReceivedFrom.contains(side) && (this.world.getTileEntity(this.getPos().add(offset)) != null))) {
                        TileEntity tileEntity = this.world.getTileEntity(this.getPos().add(offset));
                        if (tileEntity != null) {
                            if ((tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()))) {
                                if ((Integer) sides.get(side.getIndex()) == 0 || (Integer) sides.get(side.getIndex()) == 2) {
                                    sidesFluidTo.add(side);
                                }
                            } else if (tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()) && ((IFluidTankProperties) tileEntity).canFill()) {
                                if ((Integer) sides.get(side.getIndex()) == 0 || (Integer) sides.get(side.getIndex()) == 2) {
                                    sidesFluidTo.add(side);
                                }
                            }
                        }
                    }
                }
            } else if (sidesReceivedFrom.size() > 0) {
                fluidSplit = sidesReceivedFrom.size();
                int fluidtotrabsnut = (int) Math.floor(this.tank.getFluidAmount() / fluidSplit);
                for (int i = 0; i < 6; i++) {
                    EnumFacing side = EnumFacing.getFront(i);
                    Vec3i offset = new Vec3i(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
                    if (sidesReceivedFrom.contains(side) && (this.world.getTileEntity(this.getPos().add(offset)) != null)) {
                        TileEntity tileEntity = this.world.getTileEntity(this.getPos().add(offset));
                        if (tileEntity != null) {
                            if ((tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()))) {
                                if ((Integer) sides.get(side.getIndex()) == 0 || (Integer) sides.get(side.getIndex()) == 2) {
                                    sidesFluidTo.add(side);
                                }
                            } else if (tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()) && ((IFluidTankProperties) tileEntity).canFill()) {
                                if ((Integer) sides.get(side.getIndex()) == 0 || (Integer) sides.get(side.getIndex()) == 2) {
                                    sidesFluidTo.add(side);
                                }
                            }
                        }
                    }
                }
            }
            if (this.tank.getFluidAmount() > 0 && sidesFluidTo.size() > 0) {
                fluidSplit = sidesReceivedFrom.size();
                int fluidtotrabsnut = (int) Math.floor(this.tank.getFluidAmount() / fluidSplit);
                for (int i = 0; i < 6; i++) {
                    EnumFacing side = EnumFacing.getFront(i);
                    Vec3i offset = new Vec3i(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
                    if (sidesReceivedFrom.contains(side) && (this.world.getTileEntity(this.getPos().add(offset)) != null)) {
                        TileEntity tileEntity = this.world.getTileEntity(this.getPos().add(offset));
                        if (tileEntity != null) {
                            if ((tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()))) {
                                if ((Integer) sides.get(side.getIndex()) == 0 || (Integer) sides.get(side.getIndex()) == 2) {
                                    sidesFluidTo.add(side);
                                }
                            } else if (tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()) && ((IFluidTankProperties) tileEntity).canFill()) {
                                if ((Integer) sides.get(side.getIndex()) == 0 || (Integer) sides.get(side.getIndex()) == 2) {
                                    sidesFluidTo.add(side);
                                }
                            }
                        }
                    }
                }
            }
        }

        boxes.clear();
        if (this.covered) {
            boxes.add(new AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75));
        } else {
            boxes.add(new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875));
        }

        for (int i = 0; i < 6; i++) {
            if ((Integer) rendersides.get(i) < 3) {
                if (this.covered) {
                    //
                } else {
                    boxes.add(uncoveredBoxes[i]);
                }
            }
        }
        sidesReceivedFrom.clear();
        this.tank.drain(Integer.MAX_VALUE, true);
    }

    public boolean canConnect(EnumFacing side) {
        if (side.getIndex() < sides.size()) {
            return ((Integer) sides.get(side.getIndex()) < 3);
        } else {
            return false;
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        if (sides.size() == 6) {
            return this.writeToNBT(new NBTTagCompound());
        } else {
            for (int i = 0; i < 6; i++) {
                sides.add(0);
            }
            tank.writeToNBT(tag);
            return super.getUpdateTag();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        tank.readFromNBT(nbt);
        sides.clear();
        this.covered = nbt.getBoolean("covered");
        for (int i = 0; i < 6; i++) {
            sides.add(nbt.getIntArray("sides")[i]);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        tank.writeToNBT(nbt);
        int[] sidesintarray = new int[6];
        for (int i = 0; i < 6; i++) {
            sidesintarray[i] = ((Integer) sides.get(i)).intValue();
        }
        nbt.setIntArray("sides", sidesintarray);
        nbt.setBoolean("covered", this.covered);
        nbt.setString("fluid", fluid);
        return nbt;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) this;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[0];
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return null;
    }
}

