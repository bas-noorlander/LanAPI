package scripts.LanAPI.Game.Painting;

import javax.swing.*;
import java.awt.*;

/**
 * @author Laniax
 */
public class PaintString {

    private final Font _font;
    private final boolean _drawShadowed;
    private final Color _color;
    private final Color _shadowColor;
    private final String _str;
    private final Point _pos;

    public PaintString(String str) {

        this(str, new Point(0,0));
    }

    public PaintString(String str, Point pos) {

        this(str, pos, new JLabel().getFont()); //any better way to do this?
    }

    public PaintString(String str, Point pos, Font font) {

        this(str, pos, font, Color.WHITE);
    }

    public PaintString(String str, Point pos, Font font, Color fillColor) {

        this(str, pos, font, fillColor, false);
    }

    public PaintString(String str, Point pos, Font font, Color fillColor, boolean drawShadowed) {

        this(str, pos, font, fillColor, drawShadowed, Color.DARK_GRAY);
    }

    public PaintString(String str, Point pos, Font font, Color fillColor, boolean drawShadowed, Color shadowColor) {
        _str = str;
        _pos = pos;
        _font = font;
        _drawShadowed = drawShadowed;
        _color = fillColor;
        _shadowColor = shadowColor;
    }

    public Font getFont() {
        return _font;
    }

    public boolean isShadowed() {
        return _drawShadowed;
    }

    public Color getColor() {
        return _color;
    }

    public String getString() {
        return _str;
    }

    public Point getPosition() {
        return _pos;
    }
}
