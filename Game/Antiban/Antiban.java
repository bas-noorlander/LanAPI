package scripts.LanAPI.Game.Antiban;

import org.tribot.api.General;
import org.tribot.api2007.Game;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Options;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSNPC;
import scripts.LanAPI.Game.Helpers.SkillsHelper;
import scripts.LanAPI.Game.Movement.Movement;
import scripts.LanAPI.Game.Painting.PaintHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper class for Tribot's ABC system.
 *
 * @author Laniax
 */
public class Antiban extends org.tribot.api.util.ABCUtil {

    private static Antiban antiban;

    private static boolean idle = false;
    private static long idleSince = 0;

    private static int eatAtPercentage = -1;

    // singleton
    public static Antiban getUtil() {
        return antiban = antiban == null ? new Antiban() : antiban;
    }

    public static int getEatPercentage() {
        if (eatAtPercentage < 0)
            resetEatPercentage();

        return eatAtPercentage;
    }

    public static void resetEatPercentage() {
        eatAtPercentage = Antiban.getUtil().INT_TRACKER.NEXT_EAT_AT.next();
    }

    /**
     * Do all the antiban actions we are supposed to do while idling.
     */
    public static void doIdleActions() {

        SKILLS skillToCheck;

        SKILLS[] activeSkills = SkillsHelper.getAllSkillsWithIncrease();
        if (activeSkills.length == 0) {
            skillToCheck = SKILLS.values()[General.random(0, SKILLS.values().length)];
        } else
            skillToCheck = activeSkills[General.random(0, activeSkills.length - 1)];

        String preAntiban = PaintHelper.statusText;
        PaintHelper.statusText = "Antiban";
        getUtil().performTimedActions(skillToCheck);
        PaintHelper.statusText = preAntiban;

        setIdle(true);
    }

    public static void setIdle(boolean value) {

        boolean oldValue = idle;
        idle = value;

        if (value && !oldValue)
            idleSince = System.currentTimeMillis();
    }

    /**
     * Checks if our run energy is above a random threshold and toggles run on if it isn't already.
     *
     * @return if run was activated or not.
     */
    public static boolean doActivateRun() {

        if (!Game.isRunOn() && Game.getRunEnergy() >= getUtil().INT_TRACKER.NEXT_RUN_AT.next()) {

            getUtil().INT_TRACKER.NEXT_RUN_AT.reset();
            PaintHelper.statusText = "Antiban - Activate Run";

            return Options.setRunOn(true);
        }
        return false;
    }

    /**
     * Reorganizes an array of NPCs so that they are in the proper order to attack them.
     * <p>
     * Does canReach and isInCombat checks as well.
     *
     * @param npcs
     * @return the npc to attack, or null if input array was null.
     */
    public static RSNPC[] orderOfAttack(RSNPC[] npcs) {

        if (npcs.length > 0) {

            npcs = NPCs.sortByDistance(Player.getPosition(), npcs);

            List<RSNPC> orderedNPCs = new ArrayList<RSNPC>();

            for (RSNPC npc : npcs) {

                if (npc.isInCombat() || !npc.isValid() || !Movement.canReach(npc) || npc.getInteractingCharacter() != null)
                    continue;

                orderedNPCs.add(npc);
            }

            if (orderedNPCs.size() > 1) {

                if (getUtil().BOOL_TRACKER.USE_CLOSEST.next()) {

                    // if the 2nd closest npc is within 3 tiles of the closest npc, attack the 2nd one first.
                    if (orderedNPCs.get(0).getPosition().distanceTo(orderedNPCs.get(1)) <= 3)
                        Collections.swap(orderedNPCs, 0, 1);
                }

                getUtil().BOOL_TRACKER.USE_CLOSEST.reset();
            }

            return orderedNPCs.toArray(new RSNPC[orderedNPCs.size()]);
        }

        return npcs;
    }

    /**
     * Checks with the antiban if we are alowed to hover over the next object.
     *
     * @return
     */
    public static boolean mayHoverNextObject() {

        boolean result = getUtil().BOOL_TRACKER.HOVER_NEXT.next();
        getUtil().BOOL_TRACKER.HOVER_NEXT.reset();
        return result;
    }

    /**
     * Does a delay for a random amount of time.
     * Should be used when idling and a new object has spawned.
     */
    public static void doDelayForNewObject(boolean isCombat) {

        PaintHelper.statusText = "Antiban - Delay";

        DELAY_TRACKER tracker = isCombat ? getUtil().DELAY_TRACKER.NEW_OBJECT_COMBAT : getUtil().DELAY_TRACKER.NEW_OBJECT;

        General.sleep(tracker.next());
        tracker.reset();
    }

    /**
     * Does a delay for a random amount of time.
     * Should be used when an object has been drained and a new object is already available.
     */
    public static void doDelayForSwitchObject(boolean isCombat) {

        PaintHelper.statusText = "Antiban - Delay";

        DELAY_TRACKER tracker = isCombat ? getUtil().DELAY_TRACKER.SWITCH_OBJECT_COMBAT : getUtil().DELAY_TRACKER.SWITCH_OBJECT;

        General.sleep(tracker.next());
        tracker.reset();
    }

    public static void waitDelay(boolean isCombat) {
        if (idle && idleSince > (System.currentTimeMillis() + General.random(8000, 12000))) {
            // If we were idle (waiting for spawn) for more then 8-12 sec
            // we should see this as a 'new' action and wait an appropriate amount of time.
            Antiban.doDelayForNewObject(isCombat);
        } else {
            // if we were not idling (new npc was already spawned while fighting old, or within the 8-12sec after death)
            // we should see this as a 'switch' action and wait an appropriate amount of time.
            Antiban.doDelayForSwitchObject(isCombat);
        }
    }
};