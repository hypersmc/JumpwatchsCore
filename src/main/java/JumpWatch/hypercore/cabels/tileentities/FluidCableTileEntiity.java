package JumpWatch.hypercore.cabels.tileentities;

import JumpWatch.hypercore.utils.EnumCableType;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FluidCableTileEntiity extends TileEntity implements ITickable {
    protected FluidTank tank = new FluidTank(1000);
    private int max = 1000;
    private List sidesReceivedFrom = new ArrayList();
    private List sides = new IntArrayList();
    public List rendersides = new IntArrayList(); //unused for now
    public List boxes = new ArrayList(); //unused for now
    private int tempF;
    public static AxisAlignedBB[] uncoveredBoxes = {
            new AxisAlignedBB(0.3125, 0, 0.3125, 0.6875, 0.3125, 0.6875),
            new AxisAlignedBB(0.3125, 0.6875, 0.3125, 0.6875, 1, 0.6875),
            new AxisAlignedBB(0.3125, 0.3125, 0, 0.6875, 0.6875, 0.3125),
            new AxisAlignedBB(0.3125, 0.3125, 0.6875, 0.6875, 0.6875, 1),
            new AxisAlignedBB(0, 0.3125, 0.3125, 0.3125, 0.6875, 0.6875),
            new AxisAlignedBB(0.6875, 0.3125, 0.3125, 1, 0.6875, 0.6875)
    };
    public static String[] sideStates = {"no restrictions", "input only", "output only", "disabled"};
    @Override
    public int receiveFluid(EnumFacing from, FluidStack fluid, boolean simulate) {
        if (from == null) {
            int fluidReceived = tank.fillInternal(fluid, simulate);
            if (this.tank.getFluidAmount() > this.max){
                //
            }
            tempF += fluidReceived;
            if (tempF > max)
                tempF = max;
            return fluidReceived;
        }
        if ((Integer) sides.get(from.getIndex()) < 2) {
            int fluidReceived = tank.fillInternal(fluid, simulate);
            if (this.tank.getFluidAmount() > this.max){
                //
            }
            if (fluidReceived > 0) {
                sidesReceivedFrom.add(from);
            }
            tempF += fluidReceived;
            if (tempF > max)
                tempF = max;
            return fluidReceived;
        }else {
            return 0;
        }
    }
    @Override
    public int extractFluid(EnumFacing from, FluidStack fluid, boolean simulate) {
        if (from == null) {
            return Math.max(0, tank.drainInternal(fluid, simulate));
        }
    }




    @Override
    public void update() {

    }


}
