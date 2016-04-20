package scripts.lanapi.game.movement;

import org.tribot.api.General;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.util.Sorting;
import org.tribot.api.util.abc.preferences.WalkingPreference;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;
import scripts.lanapi.game.antiban.Antiban;
import scripts.lanapi.game.painting.PaintHelper;

import java.util.ArrayList;

/**
 * Helper class that manages movement logic.
 *
 * @author Laniax
 */
public class Movement {

    private static DPathNavigator nav = new DPathNavigator();

    /**
     * Checks if we can reach the specified Positionable (RSTile/RSObject/RSPlayer/RSGroundItem etc)
     *
     * @param toReach
     * @return if we can reach the positionable or not.
     */
    public static boolean canReach(final Positionable toReach) {

        return PathFinding.canReach(toReach, toReach instanceof RSObject);

    }

    /**
     * Check if a position is in the currently loaded region.
     *
     * @param pos to check
     * @return true if in the region, false otherwise.
     */
    public static boolean isInLoadedRegion(Positionable pos) {

        final RSTile base = new RSTile(Game.getBaseX(), Game.getBaseY());
        final RSArea chunk = new RSArea(base, new RSTile(base.getX() + 103, base.getY() + 103));

        return chunk.contains(pos);
    }

    /**
     * Gets all the walkable tiles in the {@link RSArea}.
     *
     * @param area
     * @return array of walkable tiles, empty if none are found.
     */
    public static RSTile[] getAllWalkableTiles(RSArea area) {

        ArrayList<RSTile> walkables = new ArrayList<RSTile>();

        for (RSTile tile : area.getAllTiles()) {
            if (PathFinding.isTileWalkable(tile))
                walkables.add(tile);
        }

        return walkables.toArray(new RSTile[walkables.size()]);
    }

    /**
     * Walks to the destination using DPathNavigator
     *
     * @param posToWalk
     * @return true if reached the destination, false otherwise.
     */
    public static boolean walkToPrecise(Positionable posToWalk) {

        // DPathNavigator cannot traverse outside the currently loaded region
        if (!isInLoadedRegion(posToWalk))
            return false;

        return nav.traverse(posToWalk);
    }

    /**
     * Walks to the destination using WebWalking
     *
     * @param posToWalk
     * @return true if reached the destination, false otherwise.
     */
    public static boolean webWalkTo(final Positionable posToWalk) {

        return WebWalking.walkTo(posToWalk, new Condition() {
            public boolean active() {
                return Player.getPosition().distanceTo(posToWalk) < 4;
            }
        }, 500);
    }

    public static void setUseCustomDoors(RSObject[] doors) {
        nav.overrideDoorCache(true, doors);
    }

    public static void setUseDefaultDoors() {
        nav.overrideDoorCache(false, null);
    }

    public static void setExcludeTiles(final Positionable[] tiles) {

        nav.setExcludeTiles(tiles);
    }

    public static Positionable[] getExcludeTiles() {

        return nav.getExcludeTiles();
    }

    /**
     * Walks to the position using either DPathNavigator for close by precision or WebWalking for greater lengths.
     *
     * Checks if run can be toggled.
     *
     * @param posToWalk
     * @return if successfully reached destination or not.
     */
    public static boolean walkTo(Positionable posToWalk) {

        if (posToWalk instanceof RSTile)
            PaintHelper.destinationTile = (RSTile) posToWalk;

        RSTile tile = posToWalk.getPosition();

        if (tile == null)
            return false;

        Antiban.activateRun();

        if (isInLoadedRegion(posToWalk)) {

            if (Antiban.getWalkingPreference(Player.getPosition().distanceTo(tile)) == WalkingPreference.SCREEN) {

                RSTile[] path = Walking.generateStraightScreenPath(tile);

                if (Walking.walkScreenPath(path, new Condition() {
                    @Override
                    public boolean active() {
                        General.sleep(50);
                        return tile.isOnScreen();
                    }
                }, General.random(3000, 4000)))
                    return true;
            }

            if (!PathFinding.isTileWalkable(posToWalk)) {

                RSArea area = new RSArea(posToWalk, 5);
                RSTile[] walkables = getAllWalkableTiles(area);

                if (walkables.length == 0)
                    return false;

                Sorting.sortByDistance(walkables, posToWalk, true);
                posToWalk = walkables[0];
            }

            return nav.traverse(posToWalk);

        } else {

            final Positionable finalPosToWalk = posToWalk;
            if (!WebWalking.walkTo(posToWalk, new Condition() {
                @Override
                public boolean active() {
                    return isInLoadedRegion(finalPosToWalk);
                }
            }, 1000)) {
                return nav.traverse(posToWalk);
            }
        }

        return false;
    }

    /**
     * Walks to the position by straight user-made paths. Will try to handle doors.
     * This is mostly used in areas which WebWalking has not mapped.
     * <p>
     * Checks if run can be toggled.
     *
     * @param path
     * @return if succesfully reached destination or not.
     */
    public static boolean walkPath(final RSTile[] path) {

        Condition stoppingCondition = new Condition() {
            @Override
            public boolean active() {
                General.sleep(500);
                return !Player.isMoving();
            }
        };

        Antiban.activateRun();

        if (Antiban.getWalkingPreference(Player.getPosition().distanceTo(path[path.length - 1])) == WalkingPreference.SCREEN) {
            return Walking.walkScreenPath(path, stoppingCondition, General.random(4000,5000));
        } else
            return Walking.walkPath(path, stoppingCondition, General.random(4000,5000));

//
//        for (int i = 0; i < path.length; i++) {
//
//            final RSTile tile = path[i];
//
//            Antiban.activateRun();
//
//            PaintHelper.destinationTile = tile;
//
//            if (canReach(tile)) {
//                Walking.blindWalkTo(tile);
//            } else {
//                nav.traverse(tile);
//            }
//
//            Timing.waitCondition(new Condition() {
//                public boolean active() {
//                    General.sleep(50);
//                    return Player.getPosition().distanceTo(tile) < 2 || !Player.isMoving();
//                }
//            }, General.random(20000, 30000));
//        }

//        return true;
    }
}
