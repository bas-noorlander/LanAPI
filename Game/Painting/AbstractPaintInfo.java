package scripts.LanAPI.Game.Painting;

import java.awt.*;
import java.util.List;

/**
 * @author Laniax
 */
public abstract class AbstractPaintInfo {

    private final Point _backgroundPosition = new Point(0, 249);

    private final Rectangle _paintToggle = new Rectangle(406, 465, 99, 26);

    private final Rectangle _settingsToggle = new Rectangle(406, 427, 99, 26);

    public abstract Image getBackground();

    public abstract Image getButtonPaintToggle();

    public abstract List<PaintString> getText(long runTime, Graphics2D g);

    public Point getBackgroundPosition() {
        return _backgroundPosition;
    }

    public Rectangle getPaintToggleRectangle() {
        return _paintToggle;
    }

    public Rectangle getSettingsToggleRectangle() {
        return _settingsToggle;
    }

    /**
     * Hook into the draw function before the text is rendered.
     * @param g
     */
    public void customDraw(Graphics2D g) {}
}
