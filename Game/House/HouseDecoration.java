package scripts.LanAPI.Game.House;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Laniax
 */
public enum HouseDecoration {

    BasicWood(1),

    BasicStone(2),

    WhitewashedStone(3),

    FremennikWood(4),

    TropicalWood(5),

    FancyStone(6),

    DeadlyMansion(7);

    private static Map<Integer, HouseDecoration> map = new HashMap<>();
    private int _bit;

    HouseDecoration(int bit) {
        this._bit = bit;
    }

    static {
        for (HouseDecoration style : HouseDecoration.values()) {
            map.put(style.getBit(), style);
        }
    }

    public int getBit() {
        return _bit;
    }

    public static HouseDecoration get(int bit) {
        return map.get(bit);
    }

}
