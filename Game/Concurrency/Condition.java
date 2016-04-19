package scripts.lanapi.game.concurrency;

import org.tribot.api.Clicking;
import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.types.RSNPC;
import scripts.lanapi.game.antiban.Antiban;
import scripts.lanapi.game.combat.Combat;
import scripts.lanapi.game.combat.Hovering;
import scripts.lanapi.game.helpers.ChooseOptionHelper;

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

    public static Condition UntilInCombat(final RSNPC npc) {
        return new Condition() {
            @Override
            public boolean active() {
                General.sleep(50, 150);
                return npc.isInCombat() || npc.isInteractingWithMe() || Combat.isUnderAttack();
            }
        };
    }

    public static Condition UntilOutOfCombatHovering(final RSNPC hoverNPC) {

        return new Condition() {
            public boolean active() {
                General.sleep(150, 300);

                if (!Antiban.isNextTargetValid(null))
                    return true;

                Combat.checkAndEat();

                if (Hovering.getShouldOpenMenu()) {

                    boolean isRightEntity = ChooseOptionHelper.isMenuOpenForEntity(hoverNPC);

                    if (!isRightEntity) {

                        if (ChooseOption.isOpen())
                            ChooseOption.close();

                        if (DynamicClicking.clickRSNPC(hoverNPC, 3)) {
                            Timing.waitMenuOpen(100);
                            return false; // let this condition run again
                        }
                    } else if (ChooseOption.isOptionValid("Attack")) {

                        ChooseOptionHelper.hover("Attack", hoverNPC);
                    }
                }
                else
                    Clicking.hover(hoverNPC);

                return Combat.getAttackingEntities().length == 0;
            }
        };
    }
}