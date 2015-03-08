package scripts.LanAPI;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;

/**
 * @author Laniax
 *
 */
public class Objects {

	/**
	 * Find the object based on its location.
	 * 
	 * @param location - the location the object is at
	 * @return The object or null
	 * 
	 */
	public static RSObject getAt(final Positionable location) {
		return org.tribot.api2007.Objects.getAt(location)[0];
	}

	/**
	 * Find the nearest object based on its model point count.
	 * Distance is set to 19.
	 * 
	 * This is CPU intensive and we should find by name if applicable.
	 * 
	 * @param modelPoints
	 * @return The object or null
	 * 
	 */
	public static RSObject findNearest(final int modelPoints) {
		return findNear(modelPoints)[0];
	}

	/**
	 * Finds nearby objects based on their model point count.
	 * Distance is set to 19.
	 * 
	 * This is CPU intensive and we should find by name if applicable.
	 * 
	 * @param modelPoints
	 * @return An array with all the objects or an empty array if there are none.
	 * 
	 */
	public static RSObject[] findNear(final int modelPoints) {
		return org.tribot.api2007.Objects.findNearest(19, Filters.Objects.modelIndexCount(modelPoints));
	}

	/**
	 * Find the nearest object based on the its actions.
	 * Distance is set to 19.
	 * 
	 * @param action
	 * @param contains - if true, will search for objects who have the action, if false will search for objects who do not.
	 * @return The object or null
	 */
	public static RSObject findNearest(final String action, final boolean contains) {		
		return findNear(action, contains)[0];
	}

	/**
	 * Finds nearby objects based on their actions.
	 * Distance is set to 19.
	 * 
	 * @param action
	 * @param contains - if true, will search for objects who have the action, if false will search for objects who do not.
	 * @return An array with all the objects or an empty array if there are none.
	 */
	public static RSObject[] findNear(final String action, final boolean contains) {		
		Filter<RSObject> filter = contains ? Filters.Objects.actionsContains(action) : Filters.Objects.actionsNotContains(action);
		return org.tribot.api2007.Objects.find(19, filter);
	}

	/**
	 * Find the nearest object based on the its name.
	 * Distance is set to 19.
	 * 
	 * @param name
	 * @return The object or null
	 */
	public static RSObject findNearest(final String name) {
		return findNear(name)[0];
	}

	/**
	 * Finds nearby objects based on their name.
	 * Distance is set to 19.
	 * 
	 * @param name
	 * @return An array with all the objects or an empty array if there are none.
	 */
	public static RSObject[] findNear(final String name) {
		return org.tribot.api2007.Objects.findNearest(19, Filters.Objects.nameEquals(name));
	}

	/**
	 * Find the nearest object based on its ID.
	 * Distance is set to 19.
	 * 
	 * @param ID, the identifying number of the object
	 * @return The object or null
	 * 
	 * @deprecated Because Object IDs should NOT be used!
	 */
	public static RSObject findNearestById(final int id) {
		return findNearById(id)[0];
	}

	/**
	 * Finds nearby objects based on the their ID.
	 * Distance is set to 19.
	 * 
	 * @param ID, the identifying number of the object
	 * @return An array with all the objects or an empty array if there are none.
	 * 
	 * @deprecated Because Object IDs should NOT be used!
	 */
	public static RSObject[] findNearById(final int id) {
		return org.tribot.api2007.Objects.findNearest(19, Filters.Objects.idEquals(id));
	}

	/**
	 * Interacts with the nearest object based on its location.
	 * 
	 * @param location, the location is object is at
	 * @param action, the action we should use.
	 */
	public static boolean interact(final RSTile location, final String action) {

		return interact(getAt(location), action);
	}

	/**
	 * Interacts with the nearest object based on its actions.
	 * 
	 * @param action, the action we should use.
	 */
	public static boolean interact(final String action) {

		return interact(findNearest(action, true), action);
	}

	/**
	 * Interacts with the nearest object based on its actions.
	 * 
	 * @param name
	 * @param action, the action we should use.
	 */
	public static boolean interact(final String name, final String action) {

		return interact(findNearest(name), action);
	}

	/**
	 * Interacts with the nearest object based on its amount of model points.
	 * 
	 * @param modelPoints, the amount of points the model has.
	 * @param action, the action we should use.
	 */
	public static boolean interact(final int modelPoints, final String action) {

		return interact(findNearest(modelPoints), action);
	}

	/**
	 * Interacts with the nearest object based on its location.
	 * 
	 * @param action, the action we should use.
	 * @param location, the location of the object
	 */
	public static boolean interact(final String action, final RSTile location) {
		
		return interact(getAt(location), action);
	}

	/**
	 * Interacts with the nearest object based on its ID.
	 * 
	 * @param action, the action we should use.
	 * @param ID, the id of the object.
	 * @deprecated Because Object IDs should NOT be used!
	 */
	public static boolean interactbyId(final int id, final String action) {

		return interact(findNearestById(id), action);
	}

	/**
	 * Interacts with an object.
	 * 
	 * @param object, the first object in the array will be used to interact with.
	 * @param action, the action we should use.
	 */
	public static boolean interact(final RSObject object, final String action) {

		if (object == null)
			return false;

		if (!Projection.isInViewport(object.getModel().getCentrePoint())) {

			if (Player.getPosition().distanceTo(object) > 3 || !Movement.canReach(object))
				Movement.walkTo(object.getPosition());

			Camera.turnToTile(object);
		}

		return object.click(action);
	}
}
