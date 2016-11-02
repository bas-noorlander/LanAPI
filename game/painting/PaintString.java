package scripts.lanapi.game.painting;


import scripts.lanapi.core.collections.Pair;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Laniax
 */
public class PaintString {

    private PaintBuilder paint_builder;
    private List<Pair<String, Color>> substrings = new ArrayList<>();
    private Color last_color = Color.WHITE;
    private int x = Integer.MIN_VALUE;
    private int y = Integer.MIN_VALUE;

    public PaintString() {

    }

    public PaintString(final PaintBuilder paint_builder) {
        this.paint_builder = paint_builder;
    }

    public void setPaintBuilder(final PaintBuilder pb) {
        this.paint_builder = pb;
    }

    public PaintString setColor(final Color clr) {
        this.last_color = clr;
        return this;
    }

    public PaintString setText(final String text, final Object... args) {
        this.substrings.add(new Pair(String.format(text, args), this.last_color));
        return this;
    }

    public PaintString setX(final int x) {
        this.x = x;
        return this;
    }

    public PaintString setY(final int y) {
        this.y = y;
        return this;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public PaintBuilder end() {
        this.paint_builder.append(this);
        return this.paint_builder;
    }

    protected List<Pair<String, Color>> getAll() {
        return this.substrings;
    }
}
