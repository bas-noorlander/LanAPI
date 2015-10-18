package scripts.LanAPI;

import java.util.ArrayList;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.util.Sorting;
import org.tribot.api2007.Game;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;

/**
 * Helper class that manages movement logic. 
 * 
 * @author Laniax
 * 
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
	 * Sets the use of custom door objects.
	 * Generally used if DPathNavigator cannot recognize a door.
	 * 
	 * Checks if run can be toggled.
	 * 
	 * @param posToWalk
	 * @param doors - Array of door objects
	 * @return if succesfully reached destination or not.
	 */
	public static void setUseCustomDoors(boolean state, RSObject[] doors) {

		if (state) {
			nav.overrideDoorCache(true, doors);

			General.println("Loaded "+doors.length+" custom doors.");
		} else {
			nav.overrideDoorCache(false, null);

			General.println("Unloaded custom doors.");
		}
	}

	/**
	 * Check if a position is in the currently loaded region.
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
	 * @param posToWalk
	 * @return true if reached the destination, false otherwise.
	 */
	public static boolean walkToPrecise(Positionable posToWalk) {

		// DPathNavigator cannot traverse outside the currently loaded region
		if (!isInLoadedRegion(posToWalk))
			return false;

		// DPathNavigator will fail if it cannot find a path, including when the target is an unwalkable tile, so try to find the nearest walkable tile.
		if (!PathFinding.isTileWalkable(posToWalk)) {

			RSArea area = new RSArea(posToWalk, 5);
			RSTile[] walkables = getAllWalkableTiles(area);

			if (walkables.length == 0)
				return false;

			Sorting.sortByDistance(walkables, posToWalk, true);
			posToWalk = walkables[0];
		}

		return nav.traverse(posToWalk);
	}

	/**
	 * Walks to the destination using WebWalking
	 * @param posToWalk
	 * @return true if reached the destination, false otherwise.
	 */
	public static boolean webWalkTo(final Positionable posToWalk) {

		return WebWalking.walkTo(posToWalk, new Condition() {
			public boolean active() {
				return Player.getPosition().distanceTo(posToWalk) < 4;
			}}, 500);
	}

	/**
	 * Walks to the position using either DPathNavigator for close by precision or WebWalking for greater lengths.
	 * if the target is unwalkable, will try to find nearest walkable tile.
	 * Checks if run can be toggled.
	 * 
	 * @param posToWalk
	 * @return if successfully reached destination or not.
	 */
	public static boolean walkTo(final Positionable posToWalk) {
		return walkTo(posToWalk, true);
	}

	/**
	 * Walks to the position using either DPathNavigator for close by precision or WebWalking for greater lengths.
	 * 
	 * Checks if run can be toggled.
	 * 
	 * @param posToWalk
	 * @param searchWalkable if the target is unwalkable, will try to find nearest walkable tile.
	 * @return if successfully reached destination or not.
	 */
	public static boolean walkTo(final Positionable posToWalk, boolean searchWalkable) {

		if (posToWalk instanceof RSTile)
			Paint.destinationTile = (RSTile)posToWalk;

		Antiban.doActivateRun();

		int failsafe = 0;
		while (!Player.getPosition().equals(posToWalk) && failsafe < 20) {

			boolean isWalkable = PathFinding.isTileWalkable(posToWalk);

			if ((isWalkable || searchWalkable) && isInLoadedRegion(posToWalk)) {
				if (walkToPrecise(posToWalk))
					return true;
			}

			if (webWalkTo(posToWalk)) 
				return true;

			failsafe++;
		}
		return false;
	}

	/**
	 * Walks to the position by straight user-made paths. Will try to handle doors.
	 * This is mostly used in areas which WebWalking has not mapped.
	 * 
	 * Checks if run can be toggled.
	 * 
	 * @param posToWalk
	 * @return if succesfully reached destination or not.
	 */
	public static boolean walkPath(final RSTile[] path) {

		General.println("Walking using hand-made paths.");

		for (int i = 0; i < path.length; i++) {

			final RSTile tile = path[i];

			Antiban.doActivateRun();

			Paint.destinationTile = tile;

			if (canReach(tile)){
				Walking.blindWalkTo(tile);
			} else {
				nav.traverse(tile);
			}

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(50);
					return Player.getPosition().distanceTo(tile) < 5;
				}}, General.random(10000, 12000));
		}

		return true;
	}
}
