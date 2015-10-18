package scripts.LanAPI;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Players;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSPlayer;

/**
 * Class for finding and dealing with other players.
 * 
 * @author Laniax
 *
 */
public class PlayersHelper { // Sadly, tribot's Players class is declared final and cannot be extended.

	/**
	 * Find the nearest player based on its name.
	 * 
	 * @param name
	 * @return The player or null
	 */
	public static RSPlayer findNearest(final String name) {
		RSPlayer[] res = findNear(name);
		return res.length > 0 ? res[0] : null;
	}

	/**
	 * Finds players that are nearby based on their name.
	 * 
	 * @param name
	 * @return An array with all the players or an empty array if there are none.
	 */
	public static RSPlayer[] findNear(final String name) {
		return Players.findNearest(Filters.Players.nameEquals(name));
	}
	
	/**
	 * Find the nearest player based on its position.
	 * 
	 * @param pos
	 * @return The player or null
	 */
	public static RSPlayer findNearest(final Positionable pos) {
		RSPlayer[] res = findNear(pos);
		return res.length > 0 ? res[0] : null;
	}

	/**
	 * Finds players that are nearby based on their position.
	 * 
	 * @param pos
	 * @return An array with all the players or an empty array if there are none.
	 */
	public static RSPlayer[] findNear(final Positionable pos) {
		return Players.findNearest(Filters.Players.tileEquals(pos));
	}

}
