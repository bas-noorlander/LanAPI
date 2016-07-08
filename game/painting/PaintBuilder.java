package scripts.lanapi.game.painting;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Laniax
 */
public class PaintBuilder {

    private List<PaintString> strings = new ArrayList<>();

    public PaintString add() {
        return new PaintString(this);
    }

    public PaintBuilder add(final PaintString ps) {
        ps.setPaintBuilder(this);
        this.append(ps);
        return this;
    }

    protected void append(final PaintString ps) {
        strings.add(ps);
    }

    public List<PaintString> getAll() {
        return this.strings;
    }

}
