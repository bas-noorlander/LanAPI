package scripts.lanapi.game.antiban;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Clickable07;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.abc.ABCProperties;
import org.tribot.api.util.abc.preferences.OpenBankPreference;
import org.tribot.api.util.abc.preferences.TabSwitchPreference;
import org.tribot.api.util.abc.preferences.WalkingPreference;
import org.tribot.api2007.*;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import scripts.lanapi.core.logging.LogProxy;
import scripts.lanapi.game.combat.Hovering;
import scripts.lanapi.game.helpers.ObjectsHelper;
import scripts.lanapi.game.movement.Movement;
import scripts.lanapi.game.painting.PaintHelper;

/**
 * Helper class for Tribot's ABC2 system.
 *
 * @author Laniax
 */
public class Antiban extends org.tribot.api.util.abc.ABCUtil {

    static LogProxy log = new LogProxy("Antiban");

    private static Antiban _instance = null;

    private int runPercentage = this.generateRunActivation();
    private int eatPercentage = this.generateEatAtHP();
    private int resourcesWon = 0;
    private int resourcesLost = 0;
    private long resourceSwitchCheckTime = Timing.currentTimeMillis() + General.random(20000, 30000);
    public long lastCombatTime = 0;

    private static Positionable nextTarget;
    private static Positionable nextTargetClosest;
    private long waitingSince;

    private Antiban() {
        // Prevent instantiation
    }

    /**
     * Returns the instance of this Antiban.
     * Note that an instance should be used per character, if your script switches to a different character,
     * you should call Antiban.get().close() to generate a new one.
     *
     * @return the antiban (ABCUtil) instance
     */
    public static Antiban get() {
        return _instance = _instance != null ? _instance : new Antiban();
    }

    /**
     * Gets the health percentage when the player should eat.
     *
     * @return
     */
    public static int getEatPercentage() {
        return get().eatPercentage;
    }

    /**
     * Generates a new percentage to eat at. This should be used right after a successful eat.
     */
    public static void generateEatPercentage() {
        get().eatPercentage = get().generateEatAtHP();
    }

    /**
     * Gets the energy percentage when the player should activate run.
     *
     * @return
     */
    public static int getRunPercentage() {
        return get().runPercentage;
    }

    /**
     * Generates a new energy percentage to run at. This should be used right after run has been toggled on.
     */
    public static void generateRunPercentage() {
        get().runPercentage = get().generateRunActivation();
    }

    /**
     * Returns if we should move to the next anticipated spawn location.
     * This should be checked ONCE when a resource is depleted and no immediate new ones are available.
     *
     * @return true if we should move to the next anticipated location, false otherwise.
     */
    public static boolean shouldMoveAnticipated() {
        return get().shouldMoveToAnticipated();
    }

    /**
     * Returns how many times we have won a resource.
     *
     * @return
     */
    public static int getResourcesWon() {
        return get().resourcesWon;
    }

    /**
     * Returns how many times we have lost a resource.
     *
     * @return
     */
    public static int getResourcesLost() {
        return get().resourcesLost;
    }

    /**
     * Returns if we should hover over the next resource, note that this should only be called once when we start interacting with each new resource.
     *
     * @return true if we should hover the next resource, false otherwise.
     */
    public static boolean shouldHoverNext() {
        return Mouse.isInBounds() && get().shouldHover();
    }

    /**
     * Returns if we should right click the next resource while hovering, note that this should only be called if we are already hovering.
     *
     * @return true if we should open the menu of the next resource, false otherwise.
     */
    public static boolean shouldOpenMenuNext() {
        return Mouse.isInBounds() && get().shouldOpenMenu();
    }

    /**
     * Generates the preferences of how a player should open the bank (by booth/banker etc).
     * NOTE: You DO NOT need to use this if you use Banking#openBank().
     *
     * @return the preference
     */
    public static OpenBankPreference getBankPreference() {
        return get().generateOpenBankPreference();
    }

    /**
     * Generates the preferences of how a player should switch game tabs (with mouse/f keys etc).
     * NOTE: You DO NOT need to use this if you use GameTab#open().
     *
     * @return the preference
     */
    public static TabSwitchPreference getTabSwitchPreference() {
        return get().generateTabSwitchPreference();
    }

    /**
     * Generates the preferences of how a player should walk (with minimap/screen etc).
     * NOTE: You DO NOT need to use this if you solely use WebWalking.
     *
     * @param distance
     * @return
     */
    public static WalkingPreference getWalkingPreference(final int distance) {
        return get().generateWalkingPreference(distance);
    }

    /**
     * Sets the current time as when we last killed an NPC.
     */
    public static void setLastCombatTime() {
        get().lastCombatTime = Timing.currentTimeMillis();
    }

    /**
     * Sets the current time as when we last did something.
     */
    public static void setWaitingSince() {
        get().waitingSince = Timing.currentTimeMillis();
    }

    /**
     * Travels to the next anticipated resource if allowed.
     *
     * @param resource - the next resource / location to run to.
     * @return true if travelled, false otherwise.
     */
    public static boolean moveToAnticipated(Positionable resource) {

        if (resource != null && shouldMoveAnticipated()) {
            get().performReactionTimeWait();

            log.info("Moving to anticipated location.");
            return Movement.walkTo(resource);
        }

        return false;
    }

    public void updateTrackers() {

        updateTrackers(false);

    }

    public void updateTrackers(final boolean fixed) {

        final boolean recentlyInCombat = (Combat.isUnderAttack() || (Timing.currentTimeMillis() - lastCombatTime < General.random(2000, 6000)));
        final boolean isHovering = Hovering.isHovering();
//        final boolean shouldOpenMenu = Hovering.getShouldOpenMenu();

        final ABCProperties props = get().getProperties();

        props.setWaitingTime(getWaitingTime());
        props.setHovering(isHovering);
        props.setMenuOpen(Hovering.isMenuOpen());
        props.setUnderAttack(recentlyInCombat);
        props.setWaitingFixed(fixed);

        get().generateTrackers();
    }

    /**
     * Performs a wait for a calculated time. Based on real human playing data.
     */
    public void performReactionTimeWait() {

        updateTrackers();

        final int reactionTime = get().generateReactionTime();

        log.info("Sleeping for %d ms.", reactionTime);

        PaintHelper.status_text = "Antiban Delay ("+Math.round(reactionTime / 1000)+"sec)";

        try {
            get().sleep(reactionTime);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }


    /**
     * Use this method to track how many times you won or lost a resource.
     * Based on this data, we will switch resources if we are losing to much.
     *
     * @param won true if we won a resource, false if not.
     */
    public static void setResourceCounter(boolean won) {

//        log.info("Resource %s", won ? "Won" : "Lost");

        if (won)
            get().resourcesWon++;
        else
            get().resourcesLost++;
    }

     /**
     * Returns if we should change resources because we are losing a lot of resources due to other players in the area.
     * Note this method should be called continuously, internally it will only check every 20-30 seconds.
     *
     * @param competitionCount the amount of players who we are competing with.
     * @return true if we should switch, false otherwise.
     */
    public static boolean switchResources(int competitionCount) {

        double win_percent = ((double) (getResourcesWon() + getResourcesLost()) / (double) getResourcesWon());

        if (50.0 > win_percent && Timing.currentTimeMillis() >= get().resourceSwitchCheckTime) {

            if (get().shouldSwitchResources(competitionCount)) {
                log.info("Determined that we should switch resources due to player competition.");
                return true;
            }

            get().resourceSwitchCheckTime = Timing.currentTimeMillis() + General.random(20000, 30000);
        }

        return false;
    }


    /**
     *
     * @return
     */
    public static int getWaitingTime() {

        int result = (int) (Timing.currentTimeMillis() - get().waitingSince);

        log.debug("GetWaitTime() %dms.", result);

        return result;
    }

    /**
     * Checks if run isn't on and we are allowed to activate it.
     * This handles the generation of new percentages as well.
     *
     * @return true if run was toggled, false if not.
     */
    public static boolean activateRun() {

        if (!Game.isRunOn() && Game.getRunEnergy() >= getRunPercentage()) {

            PaintHelper.status_text = "Antiban - Activate Run";

            if (Options.setRunOn(true)) {
                generateRunPercentage();

                return true;
            }
        }

        return false;
    }

    /**
     * Do all the antiban actions we are supposed to do while idling.
     */
    public static void doIdleActions() {

        if (get().shouldCheckTabs())
            get().checkTabs();

        if (get().shouldCheckXP())
            get().checkXP();

        if (get().shouldExamineEntity())
            get().examineEntity();

        if (get().shouldMoveMouse())
            get().moveMouse();

        if (get().shouldPickupMouse())
            get().pickupMouse();

        if (get().shouldRightClick())
            get().rightClick();

        if (get().shouldRotateCamera())
            get().rotateCamera();

        if (get().shouldLeaveGame())
            get().leaveGame();
    }

    /**
     * Gets the next target.
     *
     * @Return {@Link Positionable}, or null if we do not currently have a next
     * target.
     */
    public static <T extends Positionable> T getNextTarget() {

        if (nextTarget == null || !isNextTargetValid(null))
            return null;

        return (T) nextTarget;

    }

    /**
     * Nullifies the next target.
     */
    public void resetNextTarget() {
        nextTarget = null;
        nextTargetClosest = null;
    }

    /**
     * Determines if our current next target is still valid.
     *
     * @param possible_targets The possible next targets. This is used just in-case a new
     *                         closest target is available. If that is the case, then we can
     *                         say he next target is invalid and let the script determine a
     *                         new next target.
     * @Return boolean
     */
    public static boolean isNextTargetValid(final Positionable[] possible_targets) {

        if (nextTarget == null)
            return false;

        final RSTile pos = nextTarget.getPosition();

        if (pos == null)
            return false;

        if (!Projection.isInViewport(Projection.tileToScreen(pos, 0)))
            return false;

        if (nextTarget instanceof Clickable07 && !((Clickable07) nextTarget).isClickable())
            return false;

        if (nextTarget instanceof RSNPC && (!((RSNPC) nextTarget).isValid()) || ((RSNPC) nextTarget).isInCombat() || !Movement.canReach(nextTarget) || ((RSNPC) nextTarget).getInteractingCharacter() != null)
            return false;

        if (nextTarget instanceof RSCharacter) {
            final String name = ((RSCharacter) nextTarget).getName();
            if (name == null || name.trim().equalsIgnoreCase("null"))
                return false;
        }

        if (nextTarget instanceof RSObject) {
            if (!Objects.isAt(nextTarget, new Filter<RSObject>() {
                @Override
                public boolean accept(final RSObject o) {
                    return o.obj.equals(((RSObject) nextTarget).obj);
                }
            }))
                return false;
        }

        if (possible_targets != null && possible_targets.length > 0 && nextTargetClosest != null) {

            final RSTile new_closest_tile = possible_targets[0].getPosition();
            final RSTile orig_closest_tile = nextTargetClosest.getPosition();
            final RSTile player_pos = Player.getPosition();

            if (new_closest_tile != null && orig_closest_tile != null && player_pos != null) {

                final double new_closest_dist = new_closest_tile.distanceToDouble(player_pos);
                final double orig_closest_dist = orig_closest_tile.distanceToDouble(player_pos);

                if (new_closest_dist < orig_closest_dist)
                    return false;

            }
        }

        return true;

    }

    /**
     * Selects the next target.
     *
     * @return {@link Positionable}
     */
    public static <T extends Positionable> T determineNextTarget(final Positionable[] possible_targets) {
        try {
            if (nextTarget != null && isNextTargetValid(possible_targets))
                return (T) nextTarget;

            return (T) (nextTarget = get().selectNextTarget(possible_targets));
        } finally {
            if (nextTarget != null && possible_targets.length > 0)
                nextTargetClosest = possible_targets[0];
        }
    }

    /**
     * Hovers the next available resource, if allowed.
     *
     * @param currentlyInteracting The object you are currently interacting with.
     */
    public static void hoverNextResource(final Positionable currentlyInteracting) {
        if (currentlyInteracting == null || !shouldHoverNext())
            return;

        Positionable[] candidates = null;

        if (currentlyInteracting instanceof RSObject) {

            final RSObject interactingObject = (RSObject) currentlyInteracting;
            final String objName = ObjectsHelper.getName(interactingObject);

            if (objName == null)
                return;

            candidates = Objects.find(15, Filters.Objects.nameEquals(objName).combine(Filters.Objects.tileNotEquals(interactingObject.getPosition()), false));
        }

        if (currentlyInteracting instanceof RSNPC) {

            final RSNPC interactingNPC = (RSNPC) currentlyInteracting;

            final String name = interactingNPC.getName();
            if (name == null)
                return;

            candidates = NPCs.find(Filters.NPCs.nameEquals(name).combine(Filters.NPCs.tileNotEquals(interactingNPC.getPosition()), false));
        }

        if (candidates != null && candidates.length > 0) {

            final Positionable next = determineNextTarget(candidates);

            if (next != null && next instanceof Clickable07) {
                log.debug("Hovering over next resource");
                Hovering.setEntity((Clickable07) next);
                Hovering.setShouldOpenMenu(shouldOpenMenuNext());
            }
        }
    }
}
