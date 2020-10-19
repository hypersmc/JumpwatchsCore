package JumpWatch.hypercore.lib;

import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;

public interface IRecipeRenderer {

    int getWidth();

    int getHeight();

    String getTitle();

    void render(Minecraft mc, int xPos, int yPos, int mouseX, int mouseY);

    void renderOverlay(Minecraft mc, int mouseX, int mouseY);

    boolean handleRecipeClick(Minecraft minecraft, int mouseX, int mouseY, boolean usage);

    @Nullable
    Object getIngredientUnderMouse(int mouseX, int mouseY);
}