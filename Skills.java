package scripts.LanAPI;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.tribot.api2007.Skills.SKILLS;


/**
 * @author Laniax
 *
 */
public class Skills {

	public static LinkedHashMap<SKILLS, Integer> startSkillInfo = new LinkedHashMap<SKILLS, Integer>();

	/**
	 * Save the amount of XP of each skill so we can look back at it later (for paint, antiban etc)
	 * Make sure to call this AFTER login.
	 */
	public static void setStartSkills() {

		startSkillInfo.clear();

		for (SKILLS skill : SKILLS.values()) {
			startSkillInfo.put(skill, org.tribot.api2007.Skills.getXP(skill));
		}
	}

	/**
	 * Get the amount of XP a skill had when the script was started.
	 * @param skill
	 * @return the amount of xp
	 */
	public static int getStartXP(SKILLS skill) {
		return startSkillInfo.get(skill);
	}

	/**
	 * Checks if we received XP in a skill since the script started.
	 * @param skill to check
	 * @return true if xp was gained, false otherwise.
	 */
	public static boolean hasReceivedXP(SKILLS skill) {
		return org.tribot.api2007.Skills.getXP(skill) > startSkillInfo.get(skill);
	}

	/**
	 * Gets all the skills who's XP is higher then when we started the script
	 * @return true if xp was gained, false otherwise.
	 */
	public static SKILLS[] getAllSkillsWithIncrease() {

		ArrayList<SKILLS> list = new ArrayList<SKILLS>();

		for (SKILLS s : SKILLS.values()) {
			if (hasReceivedXP(s)){
				list.add(s);
			}
		}
		return list.toArray(new SKILLS[list.size()]);
	}
}
