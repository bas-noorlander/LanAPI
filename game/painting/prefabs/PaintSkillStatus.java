package scripts.lanapi.game.painting.prefabs;

import org.tribot.api2007.Skills;
import scripts.lanapi.core.types.StringUtils;
import scripts.lanapi.game.helpers.SkillsHelper;
import scripts.lanapi.game.painting.PaintBuilder;
import scripts.lanapi.game.painting.PaintHelper;
import scripts.lanapi.game.painting.PaintString;
import scripts.lanapi.game.persistance.Vars;
import scripts.lanapi.game.script.LANScript;

import java.awt.*;


/**
 * @author Laniax
 */
public class PaintSkillStatus extends PaintString{

    public PaintSkillStatus(final Skills.SKILLS skill) {

        LANScript script = Vars.get().get("script");

        if (script == null)
            return;

        double hours = script.getRunningTime() / 3600000.0;

        int start_xp = SkillsHelper.getStartXP(skill);
        int xp_gained = Skills.getXP(skill) - start_xp;
        int current_level = Skills.getActualLevel(skill);
        int start_level = Skills.getLevelByXP(start_xp);
        int level_gained = current_level - start_level;

        String xpHour = PaintHelper.formatNumber((int)Math.round(xp_gained / hours));

        this.setColor(Color.white)
                .setText("%s XP ", StringUtils.capitalize(skill.toString()))
                .setColor(script.paint_info.secondary)
                .setText(PaintHelper.formatNumber(xp_gained))
                .setColor(Color.white)
                .setText(" (")
                .setColor(script.paint_info.secondary)
                .setText(xpHour)
                .setColor(Color.white)
                .setText("/h) (+")
                .setColor(script.paint_info.secondary)
                .setText(String.valueOf(level_gained))
                .setColor(Color.white)
                .setText(")");
    }
}
