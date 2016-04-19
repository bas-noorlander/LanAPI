package scripts.lanapi.game.house;

import org.tribot.api2007.Game;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.types.RSVarBit;

/**
 * @author Laniax
 */
public class House {

    private static final int locationVarbit = 2187;
    private static final int decorationVarbit = 2188;

    /**
     * Checks if the player is inside his/hers house.
     * @return
     */
    public static boolean isPlayerInside() {

        return Player.getPosition().getX() > 4000; // or check for portal?

    }

    /**
     * Gets the tile of the house portal (outside) for the player's current house.
     * @return
     */
    public static RSTile getPortalTile() {

       return getPortalTile(getLocation());

    }

    /**
     * Returns the tile of the house portal (outside) for the given location.
     * @param loc
     * @return the RSTile or null.
     */
    public static RSTile getPortalTile(HouseLocation loc) {

        if (loc != null)
            return loc.getPortalTile();

        return null;
    }

    /**
     * Gets the decoration style of the house. (SimpleWood/FancyStone etc)
     * @return the decoration style or null.
     */
    public static HouseDecoration getDecoration() {

        RSVarBit var = RSVarBit.get(decorationVarbit);

        if (var == null)
            return null;

        return HouseDecoration.get(var.getValue());
    }

    /**
     * Gets the location of the house. (Rimmington/Yanille etc)
     * @return the location or null.
     */
    public static HouseLocation getLocation() {

        RSVarBit var = RSVarBit.get(locationVarbit);

        if (var == null)
            return null;

        return HouseLocation.get(var.getValue());
    }

    /**
     * Returns if we are in building mode.
     * (this is a wrapper around Game#isInBuildingMode())
     * @return
     */
    public static boolean isInBuildingMode() {
        return Game.isInBuildingMode();
    }
}
