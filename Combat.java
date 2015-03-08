package scripts.LanAPI;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api2007.Camera;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSNPC;

/**
 * @author Laniax
 *
 */
public class Combat {

	/**
	 * Checks if there is anything (or anyone) that is attacking us.
	 * Does not require the healthbar to be above our head.
	 * 
	 * @return true if we are under attack
	 */
	public static boolean isUnderAttack() {
		return org.tribot.api2007.Combat.getAttackingEntities().length > 0;
	}

	/**
	 * Check if we have food and are in need of eating
	 */
	public static void checkAndEat(final String foodName) {

		int eatAtPercentage = Antiban.getUtil().INT_TRACKER.NEXT_EAT_AT.next();

		TABS oldTab = GameTab.getOpen();
		GameTab.open(TABS.INVENTORY);
		boolean reset = false;

		while (org.tribot.api2007.Combat.getHPRatio() <= eatAtPercentage) {

			if (Clicking.click(Inventory.find(Filters.Items.nameEquals(foodName)))) {
				General.sleep(100,200);;
			}

			if (!reset) {
				Antiban.getUtil().INT_TRACKER.NEXT_EAT_AT.reset();
				reset = true;
			}
		}
		GameTab.open(oldTab);
	}

	/**
	 * Attacks the npc based on his name, can hover and attack the next npc.
	 * 
	 * @param npcs to attack (in order)
	 * @param hoverAndAttackNextTarget - True if we should hover our cursor over the next npc while fighting, however if it's true then the AntibanMgr may still override it.
	 * @return True if an npc was killed, false if not.
	 */
	public static boolean attackNPCs(final RSNPC[] npcs, final boolean hoverAndAttackNextTarget) {

		// there is no npc available.
		if (npcs == null || npcs.length == 0)
			return false;

		for (int i = 0; i < npcs.length; i++) {

			final RSNPC attackNPC = npcs[i];

			if (attackNPC.isInCombat() && !isUnderAttack())
				continue;

			if (!attackNPC.isOnScreen())
				Camera.turnToTile(attackNPC);

			// as long as we both are alive and not in combat, we try to attack him.
			for (int it = 0; it < 20; it++) {
				if (!attackNPC.isInCombat()  && !isUnderAttack() && attackNPC.isValid()) {
					if (Clicking.click("Attack", attackNPC)) {
						General.sleep(250,320);
					}
				} else break;
			}

			// someone stole our npc =(
			if (attackNPC.isInCombat() && !isUnderAttack()) {
				continue;
			}

			if (!hoverAndAttackNextTarget || !Antiban.mayHoverNextObject())
				continue;

			if (npcs.length > i+1) {

				final RSNPC hoverNPC = npcs[i+1];

				while (attackNPC.isInteractingWithMe()) {

					if (!hoverNPC.isInCombat() && hoverNPC.isValid() && Movement.canReach(hoverNPC)) {

						if (!hoverNPC.isOnScreen())
							Camera.turnToTile(hoverNPC);

						Clicking.hover(hoverNPC);

						General.sleep(190, 310);
					} else {
						continue;
					}
				}

				Antiban.doDelayForSwitchObject();

				// Here we killed our current npc, lets check if our hover target is still alive and out of combat
				if (!hoverNPC.isOnScreen())
					Camera.turnToTile(hoverNPC);

				for (int it = 0; it < 20; it++) {
					if (!hoverNPC.isInCombat() && hoverNPC.isValid() && !isUnderAttack()) {
						if (Clicking.click("Attack", hoverNPC)) {
							continue;
						}
					} else break;

					General.sleep(250,320);
				}
			}
		}
		return false;
	}
}
