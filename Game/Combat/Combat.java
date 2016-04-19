package scripts.lanapi.game.combat;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Clickable;
import org.tribot.api2007.*;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.*;
import scripts.lanapi.core.logging.LogProxy;
import scripts.lanapi.game.antiban.Antiban;
import scripts.lanapi.game.concurrency.Condition;
import scripts.lanapi.game.movement.Movement;
import scripts.lanapi.game.painting.PaintHelper;

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
     * Gets the attack style the player is currently using.
     * E.g. Chop/Slash/Rapid etc
     * @return the attack style, or null if not found.
     */
    public static String getAttackStyle() {

        String[] styles = getAvailableAttackActions();
        int index = getSelectedStyleIndex();

        if (styles.length > index) {

            return styles[index];

        }

        return null;
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

    private static boolean doAttack(final RSNPC npc) {
        Clickable menuNode = Hovering.getHoveringItem();

        if (Hovering.isHovering() && Hovering.getShouldOpenMenu() && menuNode != null)  // if we still had the menu open for this NPC as a 'hover' NPC.
            return Clicking.click(menuNode);
        else
            return Clicking.click("Attack", npc);
    }

    private static void updateCombatTrackers() {
        // Update ABC2 with average waiting time.
        final int resourcesWon = Antiban.getResourcesWon();
        final int estimateWaitingTime = resourcesWon > 0 ? (totalKillTime / resourcesWon) : 2000;

        log.debug("Generating trackers. (Resources won: %d. Est. waiting time: %dms.", resourcesWon, estimateWaitingTime);
        Antiban.get().generateTrackers(Antiban.get().generateBitFlags(estimateWaitingTime));
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

            if (doAttack(npc)) {

                if (Timing.waitCondition(Condition.UntilInCombat(npc), General.random(2000, 3000))) {

                    updateCombatTrackers();

                    if (isUnderAttack() && !npc.isInteractingWithMe()) { // A different npc is attacking.. lets attack that one instead.

                        RSCharacter[] characters = getAttackingEntities();

                        if (characters.length > 0 && characters[0] != null) {

                            if (characters[0].isInteractingWithMe() && characters[0].isInCombat()) {
                                attackNPC(npc);
                                return;
                            }
                        }
                    }

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

                            final Clickable hover = Hovering.getEntity();
                            if (hover == null || !(hover instanceof RSNPC))
                                return;

                            final RSNPC hoverNPC = (RSNPC) hover;

                            if (Timing.waitCondition(Condition.UntilOutOfCombatHovering(hoverNPC), General.random(20000, 30000))) {

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
