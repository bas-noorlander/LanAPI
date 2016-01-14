package scripts.LanAPI.Game.Combat;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Camera;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import scripts.LanAPI.Game.Antiban.Antiban;
import scripts.LanAPI.Game.Movement.Movement;
import scripts.LanAPI.Game.Painting.PaintHelper;

/**
 * @author Laniax
 */
public abstract class Combat extends org.tribot.api2007.Combat {

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
     * Attacks an NPC
     * @param npc
     * @return true if in combat with npc, false otherwise.
     */
    public static boolean attackNPC(final RSNPC npc) {

        if (npc == null)
            return false;

        if (npc.isInCombat() && !isUnderAttack())
            return false;

        if (!npc.isOnScreen())
            Camera.turnToTile(npc);

        PaintHelper.statusText = "Attacking";

        if (!npc.isInCombat() && !isUnderAttack() && npc.isValid() && Movement.canReach(npc) && npc.getInteractingCharacter() == null) {

            if (Clicking.click("Attack", npc)) {
                return Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        General.sleep(50,150);
                        return npc.isInCombat() || npc.isInteractingWithMe() || isUnderAttack();
                    }
                }, General.random(2000,3000));
            }
        }

        return false;
    }

    /**
     * Attacks the npc, can hover and attack the next npc.
     *
     * @param npcs                     to attack (in order)
     * @param hoverAndAttackNextTarget - True if we should hover our cursor over the next npc while fighting, however if it's true then the AntibanMgr may still override it.
     * @return True if an npc was killed, false if not.
     */
    public static boolean attackNPCs(final RSNPC[] npcs, final boolean hoverAndAttackNextTarget) {

        // there is no npc available.
        if (npcs == null || npcs.length == 0)
            return false;

        for (int i = 0; i < npcs.length; i++) {

            final RSNPC attackNPC = npcs[i];

            if (attackNPC(attackNPC)) {
                PaintHelper.statusText = "In Combat";

                if (!hoverAndAttackNextTarget || !Antiban.mayHoverNextObject())
                    return true;

                if (npcs.length > i + 1) {

                    PaintHelper.statusText = "Hovering Next";

                    final RSNPC hoverNPC = npcs[i + 1];

                    if (Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            General.sleep(150, 300);

                            if (hoverNPC.isInCombat() /*someone stole it*/ || !hoverNPC.isValid() /*died*/ || !Movement.canReach(hoverNPC) /*moved beyond reach*/)
                                return true;

                            Clicking.hover(hoverNPC);

                            return getAttackingEntities().length == 0;
                        }
                    }, General.random(20000, 30000))) {

                        attackNPC(hoverNPC);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
