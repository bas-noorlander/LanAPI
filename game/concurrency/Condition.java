package scripts.lanapi.game.concurrency;

import org.tribot.api.Clicking;
import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.types.RSNPC;
import scripts.lanapi.core.dynamic.Bag;
import scripts.lanapi.game.antiban.Antiban;
import scripts.lanapi.game.combat.Combat;
import scripts.lanapi.game.combat.Hovering;
import scripts.lanapi.game.helpers.ChooseOptionHelper;

import java.util.function.BooleanSupplier;

/**
 * @author Laniax
 */
public class Condition extends org.tribot.api.types.generic.Condition {

    private BooleanSupplier lambda;
    private Bag bag;

    /**
     * Create a regular Condition object.
     * Note that you should override the #active(); method.
     */
    public Condition() {
        super();
        this.lambda = null;
    }

    /**
     * Create a regular Condition object.
     * You can specify your active method in the constructor as a lambda.
     * Example: new Condition(() -> return Inventory.isEmpty());
     * @param lambda
     */
    public Condition(BooleanSupplier lambda) {
        super();
        this.lambda = lambda;
    }

    /**
     * Returns a dynamic bag that can be used to store extra data.
     * Usefull when using lambdas.
     * @return
     */
    public Bag getBag() {
        if (this.bag == null) {
            this.bag = new Bag();
        }
        return this.bag;
    }

    /**
     * Sets/replace the lambda.
     * @param lambda
     */
    public void setLambda(BooleanSupplier lambda) {
        this.lambda = lambda;
    }

    @Override
    public boolean active() {
        if (lambda != null) {
            General.sleep(50, 100);
            return lambda.getAsBoolean();
        }

        throw new RuntimeException("You have to override the active() method for a Condition object or specify a lambda in the constructor.");
    }

    /**
     * Waits for this condition with the given timeout
     * See {@link Timing#waitCondition(org.tribot.api.types.generic.Condition, long)} for more details.
     * @param timeout
     * @return
     */
    public boolean execute(long timeout) {
        return Timing.waitCondition(this, timeout);
    }

    /**
     * Waits for this condition with the given timeout, interpreted as General.random(min,max)
     * See {@link Timing#waitCondition(org.tribot.api.types.generic.Condition, long)} for more details.
     * @param min
     * @param max
     * @return
     */
    public boolean execute(int min, int max) {
        return Timing.waitCondition(this, General.random(min, max));
    }

    /**
     * Returns a condition that returns true when the bank is open and loaded.
     */
    public static Condition UntilBankOpen = new Condition(Banking::isBankScreenOpen);

    /**
     * Returns a condition that returns true when we are out of combat, this does eating and antiban internally.
     */
    public static Condition UntilOutOfCombat = new Condition(() -> {
        Combat.checkAndEat();

        if (Antiban.get().shouldLeaveGame())
            Antiban.get().leaveGame();

        return Combat.getAttackingEntities().length == 0;
    });

    /**
     * Returns a condition that returns true when we are in combat with the given npc.
     * @param npc
     * @return
     */
    public static Condition UntilInCombat(final RSNPC npc) {
        return new Condition(() -> npc.isInCombat() || npc.isInteractingWithMe() || Combat.isUnderAttack());
    }

    /**
     * Returns a condition that returns true when we are out of combat, this does eating and antiban internally, plus it will hover over the next npc.
     * @param hover_npc the npc to hover over
     * @return
     */
    public static Condition UntilOutOfCombatHovering(final RSNPC hover_npc) {

        return new Condition(() -> {
            if (!Antiban.isNextTargetValid(null))
                return true;

            Combat.checkAndEat();

            if (Hovering.getShouldOpenMenu()) {

                boolean is_right_entity = ChooseOptionHelper.isMenuOpenForEntity(hover_npc);

                if (!is_right_entity) {

                    if (ChooseOption.isOpen())
                        ChooseOption.close();

                    if (DynamicClicking.clickRSNPC(hover_npc, 3)) {
                        Timing.waitMenuOpen(100);
                        return false; // let this condition run again
                    }
                } else if (ChooseOption.isOptionValid("Attack")) {

                    ChooseOptionHelper.hover("Attack", hover_npc);
                }
            }
            else
                Clicking.hover(hover_npc);

            return Combat.getAttackingEntities().length == 0;
        });
    }
}