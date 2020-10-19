package JumpWatch.hypercore.lib;
import JumpWatch.hypercore.api.IJEIClearance;
import mezz.jei.api.*;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.ingredients.IIngredientRegistry;
import net.minecraft.client.gui.inventory.GuiContainer;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;

@JEIPlugin
public class HyperCoreJEIPlugin implements IModPlugin {

    public static IJeiRuntime jeiRuntime = null;
    public static IIngredientRegistry ingredientRegistry = null;

    @Override
    public void register(IModRegistry registry) {
        ingredientRegistry = registry.getIngredientRegistry();
        registry.addAdvancedGuiHandlers(new IAdvancedGuiHandler() {
            @Override
            public Class getGuiContainerClass() {
                return IJEIClearance.class;
            }

            @Nullable
            @Override
            public java.util.List<Rectangle> getGuiExtraAreas(GuiContainer guiContainer) {
                if (guiContainer instanceof IJEIClearance) {
                    return ((IJEIClearance) guiContainer).getGuiExtraAreas();
                }
                return new ArrayList<>();
            }
        });

    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime iJeiRuntime) {
        jeiRuntime = iJeiRuntime;
    }
}