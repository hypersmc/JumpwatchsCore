package JumpWatch.hypercore.cabels.tileentities;

import JumpWatch.hypercore.utils.EnumCableType;
import cofh.redstoneflux.api.IEnergyConnection;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.impl.EnergyStorage;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CableTileEntity extends TileEntity implements ITickable, IEnergyReceiver, IEnergyProvider, IEnergyStorage {

    protected EnergyStorage storage = new EnergyStorage(Integer.MAX_VALUE);
    private EnumCableType cableTypes;
    private int maxCapacity;
    private int loss;
    public boolean covered;
    private List sidesReceivedFrom = new ArrayList();
    private List sides = new IntArrayList();
    public List rendersides = new IntArrayList();
    public List boxes = new ArrayList();
    private int tempPower = 0;
    private int currentPower = 0;

    private static Map<EnumCableType, Integer> losses = new HashMap<EnumCableType, Integer>();
    private static Map<EnumCableType, Integer> capacities = new HashMap<EnumCableType, Integer>();

    static {
        losses.put(EnumCableType.C1, 1); capacities.put(EnumCableType.C1, 32);
        losses.put(EnumCableType.C2, 1); capacities.put(EnumCableType.C2, 64);
        losses.put(EnumCableType.C3, 1); capacities.put(EnumCableType.C3, 128);
        losses.put(EnumCableType.C4, 1); capacities.put(EnumCableType.C4, 256);
        losses.put(EnumCableType.C5, 1); capacities.put(EnumCableType.C5, 512);
        losses.put(EnumCableType.C6, 1); capacities.put(EnumCableType.C6, 1024);
        losses.put(EnumCableType.C7, 1); capacities.put(EnumCableType.C7, 2048);
        losses.put(EnumCableType.C8, 1); capacities.put(EnumCableType.C8, 4096);
        losses.put(EnumCableType.C9, 1); capacities.put(EnumCableType.C9, 8192);
        losses.put(EnumCableType.C10, 0); capacities.put(EnumCableType.C10, 16384);
        losses.put(EnumCableType.C11, 0); capacities.put(EnumCableType.C11, 32768);
        losses.put(EnumCableType.C12, 0); capacities.put(EnumCableType.C12, 65536);
        losses.put(EnumCableType.C13, 0); capacities.put(EnumCableType.C13, 131072);
        losses.put(EnumCableType.C14, 0); capacities.put(EnumCableType.C14, 262144);
        losses.put(EnumCableType.C15, 0); capacities.put(EnumCableType.C15, 524288);
        losses.put(EnumCableType.C16, 0); capacities.put(EnumCableType.C16, 1048576);
        losses.put(EnumCableType.C17, 0); capacities.put(EnumCableType.C17, 2097152);
        losses.put(EnumCableType.C18, 0); capacities.put(EnumCableType.C18, 4194304);
        losses.put(EnumCableType.C19, 0); capacities.put(EnumCableType.C19, 8388608);
        losses.put(EnumCableType.C20, 0); capacities.put(EnumCableType.C20, 16777216);
}

    public static AxisAlignedBB[] coveredBoxes = {
            new AxisAlignedBB(0.25, 0, 0.25, 0.75, 0.25, 0.75),
            new AxisAlignedBB(0.25, 0.75, 0.25, 0.75, 1, 0.75),
            new AxisAlignedBB(0.25, 0.25, 0, 0.75, 0.75, 0.25),
            new AxisAlignedBB(0.25, 0.25, 0.75, 0.75, 0.75, 1),
            new AxisAlignedBB(0, 0.25, 0.25, 0.25, 0.75, 0.75),
            new AxisAlignedBB(0.75, 0.25, 0.25, 1, 0.75, 0.75)
    };
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
        this.maxCapacity = capacities.get(type);
        if (covered)
            this.loss = (int) (losses.get(type) * 1 + 0.5);
        else
            this.loss = losses.get(type);
        this.covered = covered;
        this.cableTypes = type;
        if (sides.size() != 6) {
            for (int i = 0; i < 6; i++) {
                sides.add(0);
            }
        }

    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        if (from == null) {
            int energyReceived = storage.receiveEnergy(maxReceive, simulate);
            if (this.storage.getEnergyStored() > this.maxCapacity)
                melt();
            tempPower += energyReceived;
            if (tempPower > maxCapacity)
                tempPower = maxCapacity;
            return energyReceived;
        }
        if ((Integer) sides.get(from.getIndex()) < 2) {
            int energyReceived = storage.receiveEnergy(maxReceive, simulate);
            if (this.storage.getEnergyStored() > this.maxCapacity) {
                if (getWorld().getTileEntity(getPos().add(from.getDirectionVec())) instanceof CableTileEntity)
                    ((CableTileEntity) getWorld().getTileEntity(getPos().add(from.getDirectionVec()))).melt();
                melt();
            }
            if (energyReceived > 0) {
                sidesReceivedFrom.add(from);
            }
            tempPower += energyReceived;
            if (tempPower > maxCapacity)
                tempPower = maxCapacity;
            return energyReceived;
        } else {
            return 0;
        }
    }

    public void melt() {
        IBlockState state = this.world.getBlockState(this.getPos());
        state = Blocks.FLOWING_LAVA.getStateFromMeta(Math.max(1, (int) (8 - Math.sqrt(maxCapacity) / 16)));
        this.world.setBlockState(this.getPos(), state);
        this.world.scheduleUpdate(pos, this.world.getBlockState(pos).getBlock(), 3);
        this.world.removeTileEntity(this.getPos());
    }

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        if (from == null)
            return Math.max(0, storage.extractEnergy(maxExtract, simulate) - loss);
        if ((Integer) sides.get(from.getIndex()) == 0 || (Integer) sides.get(from.getIndex()) == 2) {
            return Math.max(0, storage.extractEnergy(maxExtract, simulate) - loss);
        } else {
            return 0;
        }
    }

    @Override
    public void update() {
        List sidesPoweredTo = new ArrayList();
        if (sides.size() != 6) {
            for (int i = 0; i < 6; i++) {
                sides.add(0);
            }
        }
        float powerSplit = 0;
        rendersides.clear();
        for (int i = 0; i < 6; i++) {
            EnumFacing side = EnumFacing.getFront(i);
            Vec3i offset = new Vec3i(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
            if (!(sidesReceivedFrom.contains(side))) {
                if (this.world.getTileEntity(this.getPos().add(offset)) != null) {
                    TileEntity tileEntity = this.world.getTileEntity(this.getPos().add(offset));
                    if (tileEntity instanceof IEnergyProvider && ((IEnergyProvider) tileEntity).canConnectEnergy(side.getOpposite()) && this.canConnectEnergy(side)) {
                        this.receiveEnergy(side, ((IEnergyProvider) tileEntity).extractEnergy(side.getOpposite(), ((IEnergyProvider) tileEntity).getMaxEnergyStored(side.getOpposite()), false), false);
                    }
                    if (tileEntity instanceof IEnergyStorage && ((IEnergyStorage) tileEntity).canExtract()) {
                        this.receiveEnergy(((IEnergyStorage) tileEntity).extractEnergy(((IEnergyStorage) tileEntity).getMaxEnergyStored(), false), false);
                    }
                }
            }
        }
        currentPower = tempPower;
        tempPower = 0;
        if (!covered) {
            //if (this.storage.getEnergyStored() > 0) {
            //    this.shockEntities();
            //}
        }
        for (int i = 0; i < 6; i++) {
            EnumFacing side = EnumFacing.getFront(i);
            Vec3i offset = new Vec3i(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
            if (!(sidesReceivedFrom.contains(side))) {
                if (this.world.getTileEntity(this.getPos().add(offset)) != null) {
                    TileEntity tileEntity = this.world.getTileEntity(this.getPos().add(offset));
                    if ((tileEntity instanceof IEnergyReceiver && ((IEnergyReceiver) tileEntity).canConnectEnergy(side.getOpposite())) || (tileEntity instanceof IEnergyStorage && ((IEnergyStorage) tileEntity).canReceive())) {
                        powerSplit++;
                        this.rendersides.add(this.sides.get(i));
                    } else {
                        if ((tileEntity instanceof IEnergyConnection && ((IEnergyConnection) tileEntity).canConnectEnergy(side.getOpposite())) || tileEntity instanceof IEnergyStorage) {
                            this.rendersides.add(this.sides.get(i));
                        } else {
                            this.rendersides.add(3);
                        }
                    }
                } else {
                    this.rendersides.add(3);
                }
            } else {
                this.rendersides.add(this.sides.get(i));
            }
        }
        if (this.storage.getEnergyStored() > 0) {
            if (powerSplit > 0) {
                int energytotransmit = (int) Math.floor(this.storage.getEnergyStored() / powerSplit);
                for (int i = 0; i < 6; i++) {
                    EnumFacing side = EnumFacing.getFront(i);
                    Vec3i offset = new Vec3i(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
                    if (!(sidesReceivedFrom.contains(side))
                            && (this.world.getTileEntity(this.getPos().add(offset)) != null)) {
                        TileEntity tileEntity = this.world.getTileEntity(this.getPos().add(offset));
                        if (tileEntity instanceof IEnergyReceiver && ((IEnergyReceiver) tileEntity).canConnectEnergy(side.getOpposite())) {
                            if ((Integer) sides.get(side.getIndex()) == 0 || (Integer) sides.get(side.getIndex()) == 2) {
                                if (this.extractEnergy(side, ((IEnergyReceiver) tileEntity).receiveEnergy(side.getOpposite(), Math.max(0, energytotransmit - loss), false), false) >= energytotransmit - loss * 2) {
                                    sidesPoweredTo.add(side);
                                }
                            }
                        } else if (tileEntity instanceof IEnergyStorage && ((IEnergyStorage) tileEntity).canReceive()) {
                            if ((Integer) sides.get(side.getIndex()) == 0 || (Integer) sides.get(side.getIndex()) == 2) {
                                if (this.extractEnergy(((IEnergyStorage) tileEntity).receiveEnergy(Math.max(0, energytotransmit - loss), false), false) >= energytotransmit - loss * 2) {
                                    sidesPoweredTo.add(side);
                                }
                            }
                        }
                    }
                }
            } else if (sidesReceivedFrom.size() > 0) {
                powerSplit = sidesReceivedFrom.size();
                int energytotransmit = (int) Math.floor(this.storage.getEnergyStored() / powerSplit);
                for (int i = 0; i < 6; i++) {
                    EnumFacing side = EnumFacing.getFront(i);
                    Vec3i offset = new Vec3i(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
                    if (sidesReceivedFrom.contains(side)
                            && (this.world.getTileEntity(this.getPos().add(offset)) != null)) {
                        TileEntity tileEntity = this.world.getTileEntity(this.getPos().add(offset));
                        if (tileEntity instanceof IEnergyReceiver && ((IEnergyReceiver) tileEntity).canConnectEnergy(side.getOpposite())) {
                            if ((Integer) sides.get(side.getIndex()) == 0 || (Integer) sides.get(side.getIndex()) == 2) {
                                if (this.extractEnergy(side, ((IEnergyReceiver) tileEntity).receiveEnergy(side.getOpposite(), Math.max(0, energytotransmit - loss), false), false) >= energytotransmit - loss * 2) {
                                    sidesPoweredTo.add(side);
                                }
                            }
                        } else if (tileEntity instanceof IEnergyStorage && ((IEnergyStorage) tileEntity).canReceive()) {
                            if ((Integer) sides.get(side.getIndex()) == 0 || (Integer) sides.get(side.getIndex()) == 2) {
                                if (this.extractEnergy(((IEnergyStorage) tileEntity).receiveEnergy(Math.max(0, energytotransmit - loss), false), false) >= energytotransmit - loss * 2) {
                                    sidesPoweredTo.add(side);
                                }
                            }
                        }
                    }
                }
            }
            if (this.storage.getEnergyStored() > loss && sidesPoweredTo.size() > 0) {
                powerSplit = sidesPoweredTo.size();
                int energytotransmit = (int) Math.floor(this.storage.getEnergyStored() / powerSplit);
                for (int i = 0; i < 6; i++) {
                    EnumFacing side = EnumFacing.getFront(i);
                    Vec3i offset = new Vec3i(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
                    if (sidesPoweredTo.contains(side)
                            && (this.world.getTileEntity(this.getPos().add(offset)) != null)) {
                        TileEntity tileEntity = this.world.getTileEntity(this.getPos().add(offset));
                        if (tileEntity instanceof IEnergyReceiver && ((IEnergyReceiver) tileEntity).canConnectEnergy(side.getOpposite())) {
                            if ((Integer) sides.get(side.getIndex()) == 0 || (Integer) sides.get(side.getIndex()) == 2) {
                                if (this.extractEnergy(side, ((IEnergyReceiver) tileEntity).receiveEnergy(side.getOpposite(), Math.max(0, energytotransmit - loss), false), false) >= energytotransmit - loss * 2) {
                                    sidesPoweredTo.add(side);
                                }
                            }
                        } else if (tileEntity instanceof IEnergyStorage && ((IEnergyStorage) tileEntity).canReceive()) {
                            if ((Integer) sides.get(side.getIndex()) == 0 || (Integer) sides.get(side.getIndex()) == 2) {
                                if (this.extractEnergy(((IEnergyStorage) tileEntity).receiveEnergy(Math.max(0, energytotransmit - loss), false), false) >= energytotransmit - loss * 2) {
                                    sidesPoweredTo.add(side);
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
                    boxes.add(coveredBoxes[i]);
                } else {
                    boxes.add(uncoveredBoxes[i]);
                }
            }
        }
        sidesReceivedFrom.clear();
        this.storage.setEnergyStored(0);
    }


    public int getCurrentPower() {
        return this.currentPower;
    }

    public void shockEntities() {
        float rangeMultiplier = Math.min(0.25f + (float) Math.sqrt(this.storage.getEnergyStored()) / 512, 0.75f);
        BlockPos rangeOffset = new BlockPos(rangeMultiplier, rangeMultiplier, rangeMultiplier);
        BlockPos maxOffset = new BlockPos(1, 1, 1);
        AxisAlignedBB range = new AxisAlignedBB(this.pos.subtract(rangeOffset), this.pos.add(maxOffset).add(rangeOffset));
        List entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, range);
        for (int i = 0; i < entities.size(); i++) {
            shock((EntityLivingBase) entities.get(i), (float) Math.sqrt(this.storage.getEnergyStored()) / 4);
        }
    }

    public void shock(EntityLivingBase target, float damage) {
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

    @Override
    public boolean canConnectEnergy(EnumFacing side) {
        if (side.getIndex() < sides.size()) {
            return ((Integer) sides.get(side.getIndex()) < 3);
        } else {
            return false;
        }
    }

    public int getSideState(int side) {
        return (Integer) sides.get(side);
    }

    public int getSideState(EnumFacing side) {
        return (Integer) sides.get(side.getIndex());
    }


    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound data = new NBTTagCompound();
        if (sides.size() == 6) {
            writeToNBT(data);
            return new SPacketUpdateTileEntity(this.pos, 1, data);
        } else {
            for (int i = 0; i < 6; i++) {
                sides.add(0);
            }
            return null;
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        world.markBlockRangeForRenderUpdate(this.pos, this.pos);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        if (sides.size() == 6) {
            return this.writeToNBT(new NBTTagCompound());
        } else {
            for (int i = 0; i < 6; i++) {
                sides.add(0);
            }
            return super.getUpdateTag();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        storage.readFromNBT(nbt);
        sides.clear();
        this.covered = nbt.getBoolean("covered");
        this.cableTypes = EnumCableType.valueOf(nbt.getString("type"));
        this.maxCapacity = capacities.get(cableTypes);
        if (covered)
            this.loss = (int) (losses.get(cableTypes) * 2 + 0.5);
        else
            this.loss = losses.get(cableTypes);
        for (int i = 0; i < 6; i++) {
            sides.add(nbt.getIntArray("sides")[i]);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        storage.writeToNBT(nbt);
        int[] sidesintarray = new int[6];
        for (int i = 0; i < 6; i++) {
            sidesintarray[i] = ((Integer)sides.get(i)).intValue();
        }
        nbt.setIntArray("sides", sidesintarray);
        nbt.setBoolean("covered", this.covered);
        nbt.setString("type", cableTypes.toString());
        return nbt;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return storage.getMaxEnergyStored();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return receiveEnergy(null, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return extractEnergy(null, maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return getEnergyStored(null);
    }

    @Override
    public int getMaxEnergyStored() {
        return getMaxEnergyStored(null);
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}