package JumpWatch.hypercore.api;
import java.awt.*;
import java.util.List;
public interface IJEIClearance {

    /**
     * This returns a list of rectangles where JEI should not render items.
     * x/y is the location of the top left of the rectangle.
     */
    List<Rectangle> getGuiExtraAreas();
}