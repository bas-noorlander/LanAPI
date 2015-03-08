package scripts.LanAPI;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
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
	 * Walks to the position using either DPathNavigator for close by precision or WebWalking for greater lengths.
	 * 
	 * Checks if run can be toggled.
	 * 
	 * @param posToWalk
	 * @return if succesfully reached destination or not.
	 */
	//public static boolean walkTo(final Positionable posToWalk) {

	//return walkToAccurate(posToWalk);
	/*Antiban.doActivateRun();

		int failsafe = 0;
		while (!Player.getPosition().equals(posToWalk) && failsafe < 20) {

			boolean faraway = false;// Player.getPosition().distanceTo(posToWalk) > 16 || posToWalk.getPosition().getPlane() != Player.getPosition().getPlane();// (!Projection.isInMinimap(Projection.tileToMinimap(posToWalk)));

			if (faraway)  {
				General.println("Using webwalking");
				if (!WebWalking.walkTo(posToWalk)){
					General.println("Webwalking failed");
					walkToAccurate(posToWalk);
				}
			}else{// if (Projection.getPosition().distanceTo(posToWalk))
				General.println("Using dpathnavigator");
				walkToAccurate(posToWalk);
			}
			if (Timing.waitCondition(new Condition() {
				public boolean active() {
					return Player.getPosition().equals(posToWalk);
				}}, General.random(1000, 2000)))
				return true;

			failsafe++;
		}

		return false;*/
	//}

	/**
	 * Walks to the position using either DPathNavigator for close by precision or WebWalking for greater lengths.
	 * 
	 * Checks if run can be toggled.
	 * 
	 * @param posToWalk
	 * @return if succesfully reached destination or not.
	 */
	public static boolean walkTo(final Positionable posToWalk) {

		if (posToWalk instanceof RSTile)
			Paint.destinationTile = (RSTile)posToWalk;

		Antiban.doActivateRun();
		
		nav.setMaxDistance(20.0);
		
		int failsafe = 0;
		while (!Player.getPosition().equals(posToWalk) && failsafe < 20) {

			if (Player.getPosition().distanceTo(posToWalk) > nav.getMaxDistance() && Player.getPosition().getPlane() == posToWalk.getPosition().getPlane())
			{
				General.println("Walking using long distance algorithm.");

				// Use webwalking until we are in range for DPathNavigator to take over
				if (!WebWalking.walkTo(posToWalk, new Condition() {
					public boolean active() {
						return Player.getPosition().distanceTo(posToWalk) <= 19; // (minimap has 19 tiles radius)
					}}, 500)) {

					General.println("Switching to walking for close by precision.");

					if (nav.traverse(posToWalk)) {

						Timing.waitCondition(new Condition() {
							public boolean active() {
								General.sleep(250);
								return !Player.isMoving();
							}}, General.random(3000, 4000));

						return true;
					}
				}
			} else {

				General.println("Walking with precision algorithm.");

				if (nav.traverse(posToWalk)) {

					Timing.waitCondition(new Condition() {
						public boolean active() {
							General.sleep(250);
							return !Player.isMoving();
						}}, General.random(7000, 8000));

					return true;
				}
			}
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

			Antiban.doActivateRun();

			Paint.destinationTile = path[i];

			if (canReach(path[i])){
				Walking.blindWalkTo(path[i]);
			} else {
				nav.traverse(path[i]);
			}

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(250);
					return !Player.isMoving();
				}}, General.random(7000, 8000));
		}

		return true;
	}
}
