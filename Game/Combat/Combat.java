package scripts.LanAPI.Game.Combat;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Clickable07;
import org.tribot.api2007.*;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.*;
import scripts.LanAPI.Core.Logging.LogProxy;
import scripts.LanAPI.Game.Antiban.Antiban;
import scripts.LanAPI.Game.Concurrency.Condition;
import scripts.LanAPI.Game.Movement.Movement;
import scripts.LanAPI.Game.Painting.PaintHelper;

/**
 * @author Laniax
 */
public abstract class Combat extends org.tribot.api2007.Combat {

    static LogProxy log = new LogProxy("Combat");

    private static int totalKillTime = 0;
    private static String foodName = "Lobster";

    /**
     * Gets the name of the food we will try to use.
     * @return
     */
    public static String getFoodName() {
        return foodName;
    }

    /**
     * Sets the name of the food we will try to use.
     * @param value
     */
    public static void setFoodName(String value) {
        foodName = value;
    }

    /**
     * Checks if we have food and are in need of eating.
     * @return true if we ate something, false otherwise
     */
    public static boolean checkAndEat() {
        return checkAndEat(foodName);
    }

    /**
     * Checks if we have food and are in need of eating.
     * @param foodName
     * @return true if we ate something, false otherwise
     */
    public static boolean checkAndEat(final String foodName) {

        RSItem[] food = Inventory.find(Filters.Items.nameEquals(foodName));

        if (food.length > 0) {

            if (getHPRatio() <= Antiban.getEatPercentage()) {

                GameTab.open(TABS.INVENTORY);

                final int preEatAmount = Inventory.getCount(foodName);

                if (Clicking.click(food[0])) {

                    if (Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            General.sleep(50, 150);
                            return preEatAmount > Inventory.getCount(foodName);
                        }
                    }, General.random(700, 1200))) {

                        Antiban.generateEatPercentage();

                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Attacks an NPC
     * @param npc
     * @return true if in combat with npc, false otherwise.
     */
    public static void attackNPC(final RSNPC npc) {

        if (npc == null)
            return;

        if (npc.isInCombat() && !isUnderAttack())
            return;

        if (!npc.isOnScreen())
            Camera.turnToTile(npc);

        Antiban.get().performReactionTimeWait();

        PaintHelper.statusText = "Attacking";

        if (!npc.isInCombat() && !isUnderAttack() && npc.isValid() && Movement.canReach(npc) && npc.getInteractingCharacter() == null) {

            if (Clicking.click("Attack", npc)) {
                if (Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        General.sleep(50, 150);

                        return npc.isInCombat() || npc.isInteractingWithMe() || isUnderAttack();
                    }
                }, General.random(2000, 3000))) {

                    // Update ABC2 with average waiting time.
                    final int resourcesWon = Antiban.getResourcesWon();
                    final int estimateWaitingTime = resourcesWon > 0 ? (totalKillTime / resourcesWon) : 2000;

                    log.debug("Generating trackers. (Resources won: %d. Est. waiting time: %dms.", resourcesWon, estimateWaitingTime);
                    Antiban.get().generateTrackers(Antiban.get().generateBitFlags(estimateWaitingTime));

                    // Determine if we have won the resource or not, and act accordingly.
                    boolean wonResource = npc.isInteractingWithMe();
                    Antiban.setResourceCounter(wonResource);

                    log.info("Resource %s", wonResource ? "Won" : "Lost");

                    if (wonResource) {

                        long combatStartTime = Timing.currentTimeMillis();

                        PaintHelper.statusText = "In Combat";

                        // Determine if we need to hover over the next NPC, if so, let Antiban determine which.
                        Antiban.hoverNextResource(npc);

                        if (Hovering.isHovering()) { // we should hover

                            final Clickable07 hover = Hovering.getEntity();
                            if (hover == null || !(hover instanceof RSNPC))
                                return;

                            final RSNPC hoverNPC = (RSNPC) hover;

                            if (Timing.waitCondition(Condition.UntilOutOfCombatHovering(hoverNPC), General.random(20000, 30000))) { //.waitForCombine(Condition.UntilOutOfCombat, General.random(20000, 30000))) {

                                if (Antiban.isNextTargetValid(null)) {
                                    // We killed our target and the hoverNPC is still valid!

                                    long killTime = Timing.currentTimeMillis() - combatStartTime;
                                    log.info("Killed a %s in %dms. (while hovering another)", npc.getName(), killTime);

                                    totalKillTime += killTime;

                                    Antiban.setLastCombatTime();
                                    Antiban.setWaitingSince();

                                    attackNPC(hoverNPC);
                                }
                            }
                        } else {
                            // We don't need to hover, simply wait until we are out of combat, and supply information to antiban.

                            if (Timing.waitCondition(Condition.UntilOutOfCombat, General.random(20000, 30000))) {
                                long killTime = Timing.currentTimeMillis() - combatStartTime;
                                log.info("Killed a %s in %dms.", npc.getName(), killTime);
                                totalKillTime += killTime;
                            }
                        }

                        Antiban.setWaitingSince();
                        Antiban.setLastCombatTime();
                        Hovering.reset();
                    }
                }
            }
        }
    }
}
