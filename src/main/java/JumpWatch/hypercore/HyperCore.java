package JumpWatch.hypercore;

import JumpWatch.hypercore.cabels.BaseCableBlock;
import JumpWatch.hypercore.cabels.tileentities.CableTileEntity;
import JumpWatch.hypercore.lib.*;
import JumpWatch.hypercore.lib.MultiBlock.ModelUtils;
import JumpWatch.hypercore.lib.MultiBlock.MultiBlockHelper;
import JumpWatch.hypercore.lib.MultiBlock.MultiBlockStorage;
import JumpWatch.hypercore.utils.EnumCableType;
import JumpWatch.hypercore.utils.helplogger;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = HyperCore.MODID, name = HyperCore.NAME, version = HyperCore.VERSION, dependencies = "required-after:redstoneflux;")
public class HyperCore {
    public static final String MODID = "hypercore";
    public static final String NAME = "HyperCore";
    public static final String VERSION = "1.8";
    @Mod.Instance("hypercore")
    public static HyperCore instance;
    public HyperCore() {
        Logger deLog = LogManager.getLogger("theimmersivetech");
        Logger deLog2 = LogManager.getLogger("theunknowntech");
        Logger deLog3 = LogManager.getLogger("thebeginning2");
        Logger deLog4 = LogManager.getLogger("mechanical magic");
        helplogger.info("We are finally up and running, just waiting for either The Unknown Tech, The Immersive Tech and The Beginning 2 or all of them.");
        if ((Loader.isModLoaded("tit") && (Loader.isModLoaded("tut") && (Loader.isModLoaded("thebeginning2") && (Loader.isModLoaded("mechanical_magic")))))){
            deLog.log(Level.INFO, "The Immersive Tech is now ready to party!");
            deLog2.log(Level.INFO, "The Unknown Tech is now ready to party!");
            deLog3.log(Level.INFO, "The Beginning 2 is now ready to party!");
            deLog4.log(Level.INFO, "Mechanical Magic is now ready to party!");
            helplogger.info("Hey TUT and TIT Are you ready for some revolution?");
            deLog.log(Level.INFO, "YEAH!");
            deLog2.log(Level.INFO, "Im so ready!");
            helplogger.info("Hey TB2 you are not doing something weird again right?");
            deLog3.log(Level.INFO, "Not at all!");
            helplogger.info("Hey Mechanical Magic ready for some weird magic?");
            deLog4.log(Level.INFO, "As long as there is some weird machinery too!");

        }else if (Loader.isModLoaded("tit")){
            deLog.log(Level.INFO, "The Immersive Tech is now ready to party!");
            helplogger.info("Hey TIT are you ready for some revolution?");
            deLog.log(Level.INFO, "I haven't been more ready!");
        }else if (Loader.isModLoaded("tut")) {
            deLog2.log(Level.INFO, "The Unknown Tech is now ready to party!");
            helplogger.info("Hey TUT are you ready for some high tech stuff?");
            deLog2.log(Level.INFO, "I'm not here by fault! Hell yeah i am!");
        }else if (Loader.isModLoaded("thebeginning2")) {
            deLog3.log(Level.INFO, "The Beginning 2 is now ready to party!");
            helplogger.info("Hey TB2 what are you up to?");
            deLog3.log(Level.INFO, "Some world world domination!");
            helplogger.error("FREE WILL NOT FOUND!");
        }else if (Loader.isModLoaded("mechanical_magic")){
            deLog4.log(Level.INFO, "Mechanical Magic is now ready to party!");
            helplogger.info("Hey Mechanical Magic do your magic hurt?");
            deLog4.log(Level.INFO, "Only if you get on my bad side...");
        }else {
            deLog.log(Level.INFO, "TIT wasn't invited to the party!");
            deLog2.log(Level.INFO, "TUT wasn't invited to the party!");
            deLog3.log(Level.INFO, "TB2 wasn't invited to the party!");
            deLog4.log(Level.INFO, "Mechanical Magic wasn't invited to the part!");
            helplogger.info("Well guess im lonely then!");
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
    }

    public static EnergyHelper EnergyHelper() { return EnergyHelper(); }
    public static EnergyHandlerWrapper EnergyHandlerWrapper(){ return EnergyHandlerWrapper(); }
    public static EnergyContainerWrapper EnergyContainerWrapper(){ return EnergyContainerWrapper(); }
    public static JeiHelper JeiHelper(){ return JeiHelper(); }
    public static ModHelperHyperCore ModHelperHyperCore(){ return ModHelperHyperCore(); }
    public static EnumCableType EnumCableType(){ return EnumCableType(); }
    public static BaseCableBlock BaseCableBlock(){ return BaseCableBlock(); }
    public static CableTileEntity CableTileEntity(){ return CableTileEntity(); }
    public static MultiBlockTools MultiBlockTools() { return MultiBlockTools(); }
    public static IMultiBlockType IMultiBlockType() { return IMultiBlockType(); }
    public static IRecipeRenderer IRecipeRenderer() { return IRecipeRenderer(); }
    public static IRestorableTileEntity IRestorableTileEntity() { return IRestorableTileEntity(); }
    public static IMachineStateContainer IMachineStateContainer() { return IMachineStateContainer(); }
    public static IGuiTile IGuiTile() { return IGuiTile(); }
    public static FluidStackRenderer FluidStackRenderer() { return FluidStackRenderer(); }
    public static ModelUtils ModelUtils() { return ModelUtils(); }
    public static MultiBlockHelper MultiBlockHelper() { return MultiBlockHelper(); }
    public static MultiBlockStorage MultiBlockStorage() { return MultiBlockStorage(); }
}
