package scripts.LanAPI.Game.Concurrency;

import org.tribot.api.Clicking;
import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api2007.Banking;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.types.RSNPC;
import scripts.LanAPI.Game.Antiban.Antiban;
import scripts.LanAPI.Game.Combat.Combat;
import scripts.LanAPI.Game.Combat.Hovering;

/**
 * @author Laniax
 */
public abstract class Condition extends org.tribot.api.types.generic.Condition {

    public static Condition UntilBankOpen = new Condition() {
        public boolean active() {
            General.sleep(150, 300);
            return Banking.isBankScreenOpen();
        }
    };

    public static Condition UntilOutOfCombat = new Condition() {
        public boolean active() {
            General.sleep(150, 300);

            Combat.checkAndEat();

            if (Antiban.get().shouldLeaveGame())
                Antiban.get().leaveGame();

            return Combat.getAttackingEntities().length == 0;
        }
    };

    public static Condition UntilOutOfCombatHovering(final RSNPC hoverNPC) {

        return new Condition() {
            public boolean active() {
                General.sleep(150, 300);

                if (!Antiban.isNextTargetValid(null))
                    return true;

                Combat.checkAndEat();

                if (Clicking.hover(hoverNPC)) {

                    if (Hovering.getShouldOpenMenu() && !ChooseOption.isOpen())
                        DynamicClicking.clickRSNPC(hoverNPC, 3);
                }

                return Combat.getAttackingEntities().length == 0;
            }
        };
    }
}