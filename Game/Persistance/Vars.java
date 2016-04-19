package scripts.lanapi.game.persistance;

import scripts.lanapi.core.dynamic.Bag;

/**
 * @author Laniax
 */
public class Vars {

    private static final Bag instance = new Bag();

    private Vars() {

    }

    public static Bag get() {
        return instance;
    }
}
