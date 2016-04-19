package scripts.lanapi.game.house;

import org.tribot.api2007.types.RSTile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Laniax
 */
public enum HouseLocation {

    Rimmington(1, new RSTile(0,0,0)),

    Taverley(2, new RSTile(2891, 3463,0)),

    Pollnivneach(3, new RSTile(0,0,0)),

    Rellekka(4, new RSTile(0,0,0)),

    Brimhaven(5, new RSTile(0,0,0)),

    Yanille(6, new RSTile(0,0,0));

    private static Map<Integer, HouseLocation> map = new HashMap<>();
    private int _bit;
    private RSTile _portalTile;

    HouseLocation(int bit, RSTile portalTile) {
        this._bit = bit;
        this._portalTile = portalTile;
    }

    static {
        for (HouseLocation loc : HouseLocation.values()) {
            map.put(loc.getBit(), loc);
        }
    }

    public int getBit() {
        return _bit;
    }

    public static HouseLocation get(int bit) {
        return map.get(bit);
    }

    public RSTile getPortalTile() {
        return _portalTile;
    }
}
