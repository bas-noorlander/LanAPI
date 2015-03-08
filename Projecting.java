package scripts.LanAPI;

import java.awt.Point;

import org.tribot.api.Screen;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;

/**
 * Some additional methods for mouse/cursor handling.
 * 
 * @author Laniax
 *
 */
public class Projecting {
	
	public static RSTile getTileAtPoint(Point p) {
		
		if (Screen.isInViewport(p)) {
			
			// No other way but to cycle thru each tile and check it.
			// The minimap has a radius of 19, so we put all the tiles 19 left/right/up/bottom from the player into an array.
			RSTile playerPos = Player.getPosition();
			RSArea area = new RSArea(playerPos.translate(-19, -19), playerPos.translate(19, 19));
			RSTile[] tilesOnScreen = area.getAllTiles();
			
			for (RSTile tile : tilesOnScreen) {
				if (Projection.getTileBoundsPoly(tile, 0).contains(p))
					return tile;
			}
		}
		
		return null;
	}

}
