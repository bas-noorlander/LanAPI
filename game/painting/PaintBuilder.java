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

    protected void append(PaintString ps) {
        strings.add(ps);
    }

    public List<PaintString> getAll() {
        return this.strings;
    }

}
