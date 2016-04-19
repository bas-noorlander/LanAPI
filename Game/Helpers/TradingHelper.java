package scripts.lanapi.game.helpers;

import org.tribot.api2007.Trading;

/**
 * @author Laniax
 */
public class TradingHelper {// Sadly, tribot's Trading class is declared final and cannot be extended.

    public static boolean isOpen() {
        return Trading.getOpponentName() != null;
    }
}
