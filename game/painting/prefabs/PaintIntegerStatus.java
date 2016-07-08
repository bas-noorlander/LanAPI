package scripts.lanapi.game.painting.prefabs;

import scripts.lanapi.game.painting.PaintHelper;
import scripts.lanapi.game.painting.PaintString;
import scripts.lanapi.game.persistance.Vars;
import scripts.lanapi.game.script.LANScript;

import java.awt.*;


/**
 * @author Laniax
 */
public class PaintIntegerStatus extends PaintString{

    public PaintIntegerStatus(final String name, final int number) {
        this(name, number, false);
    }

    public PaintIntegerStatus(final String name, final int number, boolean is_money) {

        LANScript script = Vars.get().get("script");

        if (script == null)
            return;

        double hours = script.getRunningTime() / 3600000.0;

        this.setColor(Color.white)
                .setText("%s ", name)
                .setColor(script.paint_info.secondary)
                .setText(PaintHelper.formatNumber(number, is_money))
                .setColor(Color.white)
                .setText(" (")
                .setColor(script.paint_info.secondary)
                .setText(PaintHelper.formatNumber((int) Math.round(number / hours), is_money))
                .setColor(Color.white)
                .setText("/h)");
    }
}
