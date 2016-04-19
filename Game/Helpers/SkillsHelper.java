package scripts.lanapi.game.helpers;

import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

import java.util.ArrayList;
import java.util.LinkedHashMap;


/**
 * @author Laniax
 */
public class SkillsHelper {

    public static LinkedHashMap<SKILLS, Integer> startSkillInfo = new LinkedHashMap<>();

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

            int xp = Skills.getXP(skill);
            xp = Math.max(0, xp);

            startSkillInfo.put(skill, xp);
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

        int result = startSkillInfo.containsKey(skill) ? startSkillInfo.get(skill): 0;
        result = Math.max(0, result);
        return result;
    }

    /**
     * Get's the amount of XP we gained in a skill.
     * Make sure setStartSkills() was called on this skill.
     *
     * @param skill to check
     * @return
     */
    public static int getReceivedXP(SKILLS skill) {

        int xp = Skills.getXP(skill);

        int result = 0;

        if (xp > 0) {
            result = xp - getStartXP(skill);
        }

        return result;
    }

    /**
     * Checks if we received XP in a skill since the script started.
     *
     * @param skill to check
     * @return true if xp was gained, false otherwise.
     */
    public static boolean hasReceivedXP(SKILLS skill) {
        return getReceivedXP(skill) > 0 && startSkillInfo.containsKey(skill);
    }

    /**
     * Gets all the skills who's XP is higher then when we started the script
     *
     * @return true if xp was gained, false otherwise.
     */
    public static SKILLS[] getAllSkillsWithIncrease() {

        ArrayList<SKILLS> list = new ArrayList<>();

        for (SKILLS s : SKILLS.values()) {
            if (hasReceivedXP(s)) {
                list.add(s);
            }
        }
        return list.toArray(new SKILLS[list.size()]);
    }

    /**
     * Gets the skill where we received the most XP in since we started the script.
     *
     * @return the skill, or null if none.
     */
    public static SKILLS getSkillWithMostIncrease() {

        SKILLS result = null;
        int value = Integer.MIN_VALUE;

        for (SKILLS skill : getAllSkillsWithIncrease()) {

            int increase = getReceivedXP(skill);

            if (increase > value) {
                value = increase;
                result = skill;
            }

        }

        return result;

    }
}
