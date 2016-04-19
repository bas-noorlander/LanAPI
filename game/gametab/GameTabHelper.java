package scripts.lanapi.game.gametab;

import org.tribot.api2007.types.RSVarBit;

/**
 * @author Laniax
 */
public class GameTabHelper {

    public static boolean isOpen(TABS tab) {
        RSVarBit var = RSVarBit.get(tab.varbit);
        return var != null && var.getValue() == tab.value;
    }

    public static boolean open(TABS tab) {


        return false;
    }

    public enum TABS {

        MINIGAMES(3217, 1),
        QUESTS(3217, 0),
        ACHIEVEMENT_DIARIES(3612, 1),
        KOUREND_FAVOUR(618, 1);

        private final int varbit;
        private final int value;

        TABS(int varbit, int value) {

            this.varbit = varbit;
            this.value = value;
        }
    }
}
