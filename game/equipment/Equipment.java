package scripts.lanapi.game.equipment;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.types.RSItem;
import scripts.lanapi.game.filters.Filters;
import scripts.lanapi.game.antiban.Antiban;
import scripts.lanapi.game.concurrency.Condition;
import scripts.lanapi.game.inventory.Inventory;

/**
 * @author Laniax
 */
public class Equipment extends org.tribot.api2007.Equipment {


    /**
     * Checks if the given {@link RSItem} is equipped.
     * @param item
     * @return True if equipped, false otherwise.
     */
    public static boolean isEquipped(RSItem item) {

        return isEquipped(Filters.Items.itemEquals(item));

    }

    /**
     * Returns if ALL of the items are equipped or not.
     * @param items
     * @return
     */
    public static boolean isAllEquipped(RSItem... items) {

        return find(Filters.Items.itemEquals(items)).length == items.length;

    }

    /**
     * Ensures that the given items are equipped.
     * @param items to (have) equip(ped).
     * @return true if all gear is equipped, false if failed.
     */
    public static boolean ensureEquipped(RSItem... items) {

        for (RSItem item : items) {

            if (!isEquipped(item)) {

                if (Inventory.hasItem(item))
                    if (Clicking.click("Equip", item)) {
                        Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(50,150);
                                return isEquipped(item);
                            }
                        }, General.random(2000,3000));

                        Antiban.get().performReactionTimeWait();
                    }

            }

        }
        return isAllEquipped(items);
    }
}
