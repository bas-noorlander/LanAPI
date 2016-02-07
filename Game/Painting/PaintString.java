package scripts.LanAPI.Game.Painting;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Laniax
 */
public class PaintString {

    private Font _font = new JLabel().getFont();
    private boolean _drawShadowed = false;
    private Color _color = Color.WHITE;
    private Color _shadowColor = Color.DARK_GRAY;
    private String _str;
    private Point _pos;
    private BufferedImage _icon = null;
    private Rectangle centered = null;

    public PaintString(String str) {

        this(str, new Point(0, 0));
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

    public void setIcon(BufferedImage icon) {
        _icon = icon;
    }

    public BufferedImage getIcon() {
        return _icon;
    }

    public void setCentered(Rectangle value) {
        centered = value;
    }

    public Rectangle getCentered() {
        return centered;
    }

    public Font getFont() {
        return _font;
    }
    public void setFont(Font font) {
        _font = font;
    }

    public boolean getDrawShadow() {
        return _drawShadowed;
    }

    public void setDrawShadow(boolean value) {
        _drawShadowed = value;
    }

    public Color getColor() {
        return _color;
    }

    public String getText() {
        return _str;
    }

    public Point getPosition() {
        return _pos;
    }

    public Color getShadowColor() {
        return _shadowColor;
    }

    public void setShadowColor(Color value) {
        this._shadowColor = value;
    }

    public void setText(String value) {
        this._str = value;
    }

    public void setPosition(Point value) {
        this._pos = value;
    }

    public void setColor(Color color) {
        this._color = color;
    }
}
