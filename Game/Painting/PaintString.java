package scripts.LanAPI.Game.Painting;


import scripts.LanAPI.Core.Collections.Pair;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Laniax
 */
public class PaintString {

    private final PaintBuilder paintBuilder;
    private List<Pair<String, Color>> substrings = new ArrayList<>();

    public PaintString(PaintBuilder paintBuilder) {
        this.paintBuilder = paintBuilder;
    }

    private Color lastColor = Color.WHITE;

    public PaintString setColor(Color clr) {
        this.lastColor = clr;
        return this;
    }

    public PaintString setText(String text) {
        substrings.add(new Pair(text, this.lastColor));
        return this;
    }

    public PaintBuilder end() {
        this.paintBuilder.append(this);
        return this.paintBuilder;
    }

    protected List<Pair<String, Color>> getAll() {
        return this.substrings;
    }

}
