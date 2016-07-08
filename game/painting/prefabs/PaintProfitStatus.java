package scripts.lanapi.game.painting.prefabs;

import scripts.lanapi.game.painting.PaintHelper;
import scripts.lanapi.game.painting.PaintString;
import scripts.lanapi.game.persistance.Vars;
import scripts.lanapi.game.script.LANScript;

import java.awt.*;


/**
 * @author Laniax
 */
public class PaintProfitStatus extends PaintString{

    public PaintProfitStatus() {
        LANScript script = Vars.get().get("script");

        if (script == null)
            return;

        double hours = script.getRunningTime() / 3600000.0;

        this.setColor(Color.white)
                .setText("Profit ")
                .setColor(script.paint_info.secondary)
                .setText(PaintHelper.formatNumber(PaintHelper.profit, true))
                .setColor(Color.white)
                .setText(" (")
                .setColor(script.paint_info.secondary)
                .setText(PaintHelper.formatNumber((int) Math.round(PaintHelper.profit / hours), true))
                .setColor(Color.white)
                .setText("/h)");
    }
}
