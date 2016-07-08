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

    public PaintString() {
    }

    public void setPaintBuilder(final PaintBuilder pb) {
        this.paint_builder = pb;
    }

    public PaintString(final PaintBuilder paint_builder) {
        this.paint_builder = paint_builder;
    }

    private Color last_color = Color.WHITE;

    public PaintString setColor(final Color clr) {
        this.last_color = clr;
        return this;
    }

    public PaintString setText(final String text, final Object... args) {
        this.substrings.add(new Pair(String.format(text, args), this.last_color));
        return this;
    }

    public PaintBuilder end() {
        this.paint_builder.append(this);
        return this.paint_builder;
    }

    protected List<Pair<String, Color>> getAll() {
        return this.substrings;
    }

}
