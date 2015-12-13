package scripts.LanAPI;

import org.tribot.api.Clicking;
import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api2007.Camera;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;

/**
 * @author Laniax
 *
 */
public abstract class Combat extends org.tribot.api2007.Combat{

	/**
	 * Check if we have food and are in need of eating
	 */
	public static void checkAndEat(final String foodName) {
		
		RSItem[] food = Inventory.find(Filters.Items.nameEquals(foodName));
		
		if (food != null && food.length > 0) {
			
			if (getHPRatio() <= Antiban.getEatPercentage() || Inventory.isFull()) {
				
				GameTab.open(TABS.INVENTORY);
				
				if (Clicking.click(food[0]))
					General.sleep(200, 300);

				Antiban.resetEatPercentage();
			}
		}
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
			
			Paint.statusText = "Attacking";

			// as long as we both are alive and not in combat, we try to attack him.
			for (int it = 0; it < 20; it++) {
				if (!attackNPC.isInCombat() && !isUnderAttack() && attackNPC.isValid() && Movement.canReach(attackNPC)) {
					if (DynamicClicking.clickRSNPC(attackNPC, "Attack"));
						General.sleep(750,1320);
				} else 
					break;
			}

			// someone stole our npc =(
			if (attackNPC.isInCombat() && !isUnderAttack()) {
				continue;
			}

			if (!hoverAndAttackNextTarget || !Antiban.mayHoverNextObject())
				break;

			if (npcs.length > i+1) {
				
				final RSNPC hoverNPC = npcs[i+1];
				
				RSCharacter[] attackingCharacters = getAttackingEntities();
			
				// Hovering over next npc as long as we are still attacking the current npc and hover npc is valid.
				while (attackingCharacters.length > 0 && attackingCharacters[0] == attackNPC) {

					if (!hoverNPC.isInCombat() && hoverNPC.isValid() && Movement.canReach(hoverNPC)) {

						if (!hoverNPC.isOnScreen())
							Camera.turnToTile(hoverNPC);

						Clicking.hover(hoverNPC);

						General.sleep(100, 200);
					} else {
						break;
					}
					
					attackingCharacters = getAttackingEntities();
				}

				Antiban.doDelayForSwitchObject();

				// Here we killed our current npc, lets check if our hover target is still alive and out of combat
				if (!hoverNPC.isOnScreen())
					Camera.turnToTile(hoverNPC);
				
				Paint.statusText = "Attacking";

				for (int it = 0; it < 20; it++) {
					if (!hoverNPC.isInCombat() && hoverNPC.isValid() && !isUnderAttack() && Movement.canReach(hoverNPC)) {
						DynamicClicking.clickRSNPC(hoverNPC, "Attack");
						General.sleep(250,320);
					} else 
						break;
				}
			}
		}
		return false;
	}
}
