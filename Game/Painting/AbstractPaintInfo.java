package scripts.LanAPI.Game.Painting;

import java.awt.*;
import java.util.List;

/**
 * @author Laniax
 */
public abstract class AbstractPaintInfo {

    private Point _backgroundPosition = new Point(0, 249);

    private Rectangle _paintToggle = new Rectangle(406, 465, 99, 26);

    private Rectangle _settingsToggle = new Rectangle(406, 427, 99, 26);

    public abstract Image getBackground();

    public abstract Image getButtonPaintToggle();

    public abstract List<PaintString> getText(long runTime, Graphics2D g);

    public Point getBackgroundPosition() {
        return _backgroundPosition;
    }

    public void setBackgroundPosition(Point value) {
        _backgroundPosition = value;
    }

    public void setPaintToggleRectangle(Rectangle value) {
        _paintToggle = value;
    }

    public void setSettingsToggleRectangle(Rectangle value) {
        _settingsToggle = value;
    }

    public Rectangle getPaintToggleRectangle() {
        return _paintToggle;
    }

    public Rectangle getSettingsToggleRectangle() {
        return _settingsToggle;
    }

    /**
     * Hook into the draw function after all the text is rendered.
     * @param g
     */
    public void customDraw(Graphics2D g) {}
}
