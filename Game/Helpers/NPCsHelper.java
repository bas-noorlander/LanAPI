package scripts.lanapi.game.helpers;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Camera;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSNPCDefinition;
import scripts.lanapi.core.logging.LogProxy;
import scripts.lanapi.game.movement.Movement;

/**
 * Helper class that manages NPC logic.
 *
 * @author Laniax
 */
public class NPCsHelper { // Sadly, tribot's NPCs class is declared final and cannot be extended.

    static LogProxy log = new LogProxy("NPCsHelper");


    public static String getName(final RSNPC npc) {
        RSNPCDefinition definition = npc.getDefinition();
        if (definition != null) {
            String definitionName = definition.getName();
            if (definitionName != null) {
                return definitionName;
            }
        }

        return null;
    }

    /**
     * Find the nearest npc based on its name.
     *
     * @param name
     * @return The npc or null
     */
    public static RSNPC findNearest(final String name) {
        RSNPC[] res = findNear(name);
        return res.length > 0 ? res[0] : null;
    }

    /**
     * Finds npcs that are nearby based on their name.
     *
     * @param name
     * @return An array with all the npcs or an empty array if there are none.
     */
    public static RSNPC[] findNear(final String... name) {
        return NPCs.findNearest(Filters.NPCs.nameEquals(name));
    }

    /**
     * Find the nearest npc based on its model point count.
     * This is CPU intensive and we should find by name if applicable.
     *
     * @param modelPoints the amount of points the model has, as viewed with the debug tool.
     * @return The npc or null
     */
    public static RSNPC findNearest(final int modelPoints) {
        RSNPC[] res = findNear(modelPoints);
        return res.length > 0 ? res[0] : null;
    }

    /**
     * Finds npcs that are nearby based on their model point count.
     * This is CPU intensive and we should find by name if applicable.
     *
     * @param modelPoints the amount of points the model has, as viewed with the debug tool.
     * @return An array with all the npcs or an empty array if there are none.
     */
    public static RSNPC[] findNear(final int modelPoints) {
        return NPCs.findNearest(Filters.NPCs.modelIndexCount(modelPoints));
    }

    /**
     * Find the nearest npc based on its available actions.
     *
     * @param action
     * @param contains - if true, will search for npcs who have the action, if false will search for npcs who do not.
     * @return The npc or null
     */
    public static RSNPC findNearest(final String action, final boolean contains) {
        RSNPC[] res = findNear(action, contains);
        return res.length > 0 ? res[0] : null;
    }

    /**
     * Find npcs that are nearby based on its available actions.
     *
     * @param action
     * @param contains - if true, will search for npcs who have the action, if false will search for npcs who do not.
     * @return An array with all the npcs or an empty array if there are none.
     */
    public static RSNPC[] findNear(final String action, final boolean contains) {
        Filter<RSNPC> filter = contains ? Filters.NPCs.actionsContains(action) : Filters.NPCs.actionsNotContains(action);
        return NPCs.find(filter);
    }

    /**
     * Find all the npcs on the specified position.
     *
     * @param pos
     * @return An array with all the npcs or an empty array if there are none.
     */
    public static RSNPC[] getAt(final Positionable pos) {
        return NPCs.find(Filters.NPCs.tileEquals(pos));
    }

    /**
     * Interacts with the nearest NPC based on its name.
     *
     * @param name
     */
    public static boolean talkTo(final String name) {
        RSNPC npc = findNearest(name);
        if (npc != null)
            return talkTo(npc);

        return false;
    }

    /**
     * Interacts with the nearest NPC based on its actions.
     *
     * @param action   - the action we should use.
     * @param contains - if true, will search for npcs who have the action, if false will search for npcs who do not.
     */
    public static boolean talkTo(final String action, final boolean contains) {
        RSNPC npc = findNearest(action, contains);
        if (npc != null)
            return talkTo(npc);

        return false;
    }

    /**
     * Interacts with the nearest NPC based on its amount of model points.
     *
     * @param modelPoints - the amount of points the npc has.
     */
    public static boolean talkTo(final int modelPoints) {
        RSNPC npc = findNearest(modelPoints);
        if (npc != null)
            return talkTo(npc);

        return false;
    }

    /**
     * Talks to the npc.
     *
     * @param npc
     * @return true if talking to the npc, false if npc is null or something went wrong.
     */
    public static boolean talkTo(final RSNPC npc) {

        if (npc == null)
            return false;

        while (NPCChat.getMessage() == null) {

            if (!npc.isOnScreen()) {

                if (Player.getPosition().distanceTo(npc) > 5 || !Movement.canReach(npc))
                    Movement.walkTo(npc.getPosition());

                Camera.turnToTile(npc);
            }

            if (Clicking.click("Talk-to", npc)) {

                if (Timing.waitCondition(new Condition() {
                    public boolean active() {
                        General.sleep(50);
                        return NPCChat.getMessage() != null;
                    }
                }, General.random(400, 600))) {

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Talks to the NPC until the option becomes available and clicks it.
     *
     * @param option to click
     * @return true if successfully clicked the option.
     */
    public static boolean talkToOption(final String option) {

        talkContinue();

        return NPCChat.selectOption(option, true);
    }


    /**
     * Talks to the NPC using the 'Continue to read' function until it has nothing to say anymore.
     *
     * @return true when done.
     */
    public static boolean talkContinue() {

        String str = NPCChat.getMessage();
        String[] options = NPCChat.getOptions();

        while (str != null && options == null) {

            log.info("DIALOG TEXT: '%s'.", str);

            NPCChat.clickContinue(true);

            str = NPCChat.getMessage();
            options = NPCChat.getOptions();

        }

        Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                return NPCChat.getOptions() != null;
            }
        }, General.random(250, 500));

        options = NPCChat.getOptions();

        if (options != null) {
            for (String op : options) {
                log.info("DIALOG OPTION: '%s'.", op);
            }
        }

        return true;
    }
}
