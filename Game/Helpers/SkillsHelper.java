package scripts.LanAPI.Game.Helpers;

import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

import java.util.ArrayList;
import java.util.LinkedHashMap;


/**
 * @author Laniax
 */
public class SkillsHelper {

    public static LinkedHashMap<SKILLS, Integer> startSkillInfo = new LinkedHashMap<SKILLS, Integer>();

    /**
     * Save the amount of XP of each skill so we can look back at it later (for paint, antiban etc)
     * Make sure to call this AFTER login.
     */
    public static void setStartSkills() {
        setStartSkills(SKILLS.values());
    }

    /**
     * Save the amount of XP of specific skills so we can look back at it later (for paint, antiban etc)
     * Make sure to call this AFTER login.
     *
     * @param skills - array of skills to save.
     */
    public static void setStartSkills(SKILLS[] skills) {

        startSkillInfo.clear();

        for (SKILLS skill : skills) {
            startSkillInfo.put(skill, Skills.getXP(skill));
        }
    }

    /**
     * Get the list of all skills and start XP which were set by setStartSkills()
     *
     * @return the amount of xp
     */
    public static LinkedHashMap<SKILLS, Integer> getStartSkills() {
        return startSkillInfo;
    }

    /**
     * Get the amount of XP a skill had when the script was started.
     *
     * @param skill
     * @return the amount of xp
     */
    public static int getStartXP(SKILLS skill) {
        if (startSkillInfo.containsKey(skill))
            return startSkillInfo.get(skill);
        return 0;
    }

    /**
     * Get's the amount of XP we gained in a skill.
     * Make sure setStartSkills() was called on this skill.
     *
     * @param skill to check
     * @return true if xp was gained, false otherwise.
     */
    public static int getReceivedXP(SKILLS skill) {
        return Skills.getXP(skill) - getStartXP(skill);
    }

    /**
     * Checks if we received XP in a skill since the script started.
     *
     * @param skill to check
     * @return true if xp was gained, false otherwise.
     */
    public static boolean hasReceivedXP(SKILLS skill) {
        return getReceivedXP(skill) > 0;
    }

    /**
     * Gets all the skills who's XP is higher then when we started the script
     *
     * @return true if xp was gained, false otherwise.
     */
    public static SKILLS[] getAllSkillsWithIncrease() {

        ArrayList<SKILLS> list = new ArrayList<SKILLS>();

        for (SKILLS s : SKILLS.values()) {
            if (hasReceivedXP(s)) {
                list.add(s);
            }
        }
        return list.toArray(new SKILLS[list.size()]);
    }
}
