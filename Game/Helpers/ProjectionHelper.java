package scripts.lanapi.game.helpers;

import org.tribot.api.Screen;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;

import java.awt.*;

/**
 * Some additional methods for mouse/cursor handling.
 *
 * @author Laniax
 */
public class ProjectionHelper { // Sadly, tribot's Projection class is declared final and cannot be extended.

    public static RSTile getTileAtPoint(Point p) {

        if (Screen.isInViewport(p)) {

            // No other way but to cycle thru each tile and check it.
            // The minimap has a radius of 19, so we put all the tiles 19 left/right/up/bottom from the player into an array.
            RSTile playerPos = Player.getPosition();
            RSArea area = new RSArea(playerPos.translate(-19, -19), playerPos.translate(19, 19));
            RSTile[] tilesOnScreen = area.getAllTiles();

            for (RSTile tile : tilesOnScreen) {

                if (tile != null && tile.isOnScreen()) {

                    Polygon poly = Projection.getTileBoundsPoly(tile, 0);
                    if (poly != null && poly.contains(p))
                        return tile;
                }
            }
        }

        return null;
    }

    public static RSTile getTileAtMinimapPoint(Point p) {

        if (Projection.isInMinimap(p)) {

            // No other way but to cycle thru each tile and check it.
            // The minimap has a radius of 19, so we put all the tiles 19 left/right/up/bottom from the player into an array.
            RSTile playerPos = Player.getPosition();
            RSArea area = new RSArea(playerPos.translate(-19, -19), playerPos.translate(19, 19));
            RSTile[] tilesOnScreen = area.getAllTiles();

            for (RSTile tile : tilesOnScreen) {

                if (tile != null) {

                    Point mPoint = Projection.tileToMinimap(tile);

                    // Check if the points are near, since we probably wont get an exact match.
                    double dist = Math.sqrt((mPoint.x - p.x) * (mPoint.x - p.x) + (mPoint.y - p.y) * (mPoint.y - p.y));
                    if (dist < 3)
                        return tile;
                }
            }
        }

        return null;
    }

    public static boolean isInMinimap(Positionable t) {

        // minimap is 19 squares radius around you(center)
        return Player.getPosition().distanceTo(t) < 20;

    }
}
