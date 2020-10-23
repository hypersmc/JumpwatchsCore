package JumpWatch.hypercore.cabels;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.util.math.AxisAlignedBB;

public class FluidBaseCableBlock extends Block {
    static final IProperty<Boolean> down = PropertyBool.create("down");
    static final IProperty<Boolean> up = PropertyBool.create("up");
    static final IProperty<Boolean> north = PropertyBool.create("north");
    static final IProperty<Boolean> south = PropertyBool.create("south");
    static final IProperty<Boolean> west = PropertyBool.create("west");
    static final IProperty<Boolean> east = PropertyBool.create("east");
    protected AxisAlignedBB boundbox;
    protected int lookingSide;

    public v

}
