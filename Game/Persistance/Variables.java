package scripts.LanAPI.Game.Persistance;

import scripts.LanAPI.Core.Dynamic.Bag;

/**
 * @author Laniax
 */
public class Variables {

    private static final Bag instance = new Bag();

    private Variables() {

    }

    public static Bag getInstance() {
        return instance;
    }
}
