package JumpWatch.hypercore;

import net.minecraftforge.common.config.Config;

@Config(modid = HyperCore.MODID)
public class ModSettings {
    public static FluidCableProperties fluidCableProperties = new FluidCableProperties();

    public static class FluidCableProperties {
        @Config.Name("Cable Max Fluid Amount")
        @Config.Comment({"How much fluid each cable can contain. [Default: 1000]"})
        public int FluidAmount = 1000;
        @Config.Name("Cable Max Fluid transfer")
        @Config.Comment({"How much fluid each cable can transfer. [Default: 100]"})
        public int FluidTransfer = 100;
    }
}
