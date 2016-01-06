package scripts.LanAPI.Game.Inventory;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.types.RSItem;

/**
 * @author Laniax
 */
public class Inventory extends org.tribot.api2007.Inventory {

    /**
     * Returns if the inventory is empty or not.
     *
     * @return true if the inventory is empty, false otherwise.
     */
    public static boolean isEmpty() {

        return Inventory.getAll().length == 0;
    }

    /**
     * Returns how many free spaces the player has in its inventory.
     *
     * @return the amount of free spaces
     */
    public static int getAmountOfFreeSpace() {

        return 28 - Inventory.getAll().length;
    }


    /**
     * Returns if we have one of the item names in our inventory
     *
     * @param itemNames
     * @return true if we have one of the items, false otherwise.
     */
    public static boolean hasItem(final String... itemNames) {

        return Inventory.getCount(itemNames) > 0;
    }

    /**
     * Returns if we have one of the item IDs in our inventory
     *
     * @param itemIDs
     * @return true if we have one of the items, false otherwise.
     */
    public static boolean hasItem(final int... itemIDs) {

        return Inventory.getCount(itemIDs) > 0;
    }

    public static int getCount(RSItem item) {

        return item != null ? Inventory.getCount(item.getID()) : 0;
    }

    /**
     * Returns the number of items in the inventory which are accepted by the filter.
     *
     * @param filter
     * @return the number of items, takes stack size into account.
     */
    public static int getCount(Filter<RSItem> filter) {

        int result = 0;

        for (RSItem item : Inventory.find(filter)) {
            result += item.getStack();
        }

        return result;
    }
}