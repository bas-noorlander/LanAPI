package scripts.lanapi.game.inventory;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.types.RSItem;
import scripts.lanapi.game.filters.Filters;

/**
 * @author Laniax
 */
public class Inventory extends org.tribot.api2007.Inventory {

    /**
     * Returns if we have (ALL of) the item(s) in our inventory..
     * @param items
     * @return
     */
    public static boolean hasItem(RSItem... items) {

        return find(Filters.Items.itemEquals(items)).length == items.length;

    }

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
    
    /**
     * Gets row of given item in inventory.
     *
     * @param rsItem
     * @return row of item (0-6) or if item null or not in inventory -1
     */
    public static int getItemRow(final RSItem rsItem) {
        
        return (rsItem != null && hasItem(rsItem)) ? getItemRowUnsafe(rsItem) : -1;
    }

    /**
     * Gets row of given item in inventory.
     * Doesn't check for null or if item is in inventory.
     *
     * @param rsItem
     * @return row of item (0-6)
     */
    private static int getItemRowUnsafe(final RSItem rsItem) {
        
        return (rsItem.getIndex() - getItemColumnUnsafe(rsItem)) / 4;
    }

    /**
     * Gets column of given item in inventory.
     *
     * @param rsItem
     * @return column of item (0-3) or if item null or not in inventory -1
     */
    public static int getItemColumn(final RSItem rsItem) {
        
        return (rsItem != null && hasItem(rsItem)) ? getItemColumnUnsafe(rsItem) : -1;
    }

    /**
     * Gets column of given item in inventory.
     * Doesn't check for null or if item is in inventory.
     *
     * @param rsItem
     * @return column of item (0-3)
     */
    private static int getItemColumnUnsafe(final RSItem rsItem) {
        
        return rsItem.getIndex() % 4;
    }

    /**
     * Gets items which are contained within the first containing column.
     *
     * @param items Items to get those that are contained within lowest containing column.
     * @return items contained within the first containing column from given items.
     */
    public static RSItem[] getItemsInLowestColumn(final RSItem... items) {
        
        return getItemsInColumn(getLowestContainingColumn(items), items);
    }

    /**
     * Gets items which are contained within the given column.
     *
     * @param column Column to get items contain within (0-3).
     * @param items  Items to get those that are contained within given column.
     * @return items within given column from given items.
     */
    public static RSItem[] getItemsInColumn(final int column, final RSItem... items) {
        
        if (items != null && column >= 0 && column <= 3) {
            List<RSItem> itemList = new ArrayList<>();

            for (RSItem item : items) {
                int col = getItemColumn(item);
                if (col == column) {
                    itemList.add(item);
                }
            }
            return itemList.toArray(new RSItem[itemList.size()]);
        }
        
        return new RSItem[0];
    }

    /**
     * Gets lowest column which contains any given items
     *
     * @param items Items to get lowest containing column of.
     * @return Lowest column which contains any given items (0-3), -1 if items is null or empty or none are in inventory.
     */
    public static int getLowestContainingColumn(final RSItem... items) {
        
        int lowestCol = -1;
        if (items != null) {
            for (RSItem item : items) {
                int col = getItemColumn(item);
                if (col < lowestCol || lowestCol == -1) {
                    lowestCol = col;
                }
            }
        }
        
        return lowestCol;
    }

    /**
     * Gets items which are contained within the first containing row.
     *
     * @param items Items to get those that are contained within lowest containing row.
     * @return items contained within the first containing row from given items.
     */
    public static RSItem[] getItemsInLowestRow(final RSItem... items) {
        
        return getItemsInRow(getLowestContainingRow(items), items);
    }

    /**
     * Gets items which are contained within the given row.
     *
     * @param row   Row to get items contain within (0-6).
     * @param items Items to get those that are contained within given row.
     * @return items within given row from given items.
     */
    public static RSItem[] getItemsInRow(final int row, final RSItem... items) {
        
        if (items != null && row >= 0 && row <= 6) {
            List<RSItem> itemList = new ArrayList<>();

            for (RSItem item : items) {
                int r = getItemRow(item);
                if (r == row) {
                    itemList.add(item);
                }
            }
            return itemList.toArray(new RSItem[itemList.size()]);
        }
        
        return new RSItem[0];
    }

    /**
     * Gets lowest row which contains any given items
     *
     * @param items Items to get lowest containing row of.
     * @return Lowest row which contains any given items (0-6), -1 if items is null or empty or none are in inventory.
     */
    public static int getLowestContainingRow(final RSItem... items) {
        
        int lowestRow = -1;
        
        if (items != null) {
            for (RSItem item : items) {
                int r = getItemRow(item);
                if (r < lowestRow || lowestRow == -1) {
                    lowestRow = r;
                }
            }
        }
        
        return lowestRow;
    }

    /**
     * Gets items in lowest containing rectangle of items of given width and height.
     *
     * @param width  Width of rectangle.
     * @param height Height of rectangle.
     * @param items  Items to get those contained within rectangle from.
     * @return Items contained in lowest containing rectangle of items with given width/height in inventory.
     */
    public static RSItem[] getItemsInLowestRectangle(final int width, final int height, final RSItem... items) {
        
        return getItemsInRectangle(new Rectangle(getLowestContainingRow(items), getLowestContainingColumn(items), width, height), items);
    }

    /**
     * Gets given items contained within given rectangle of items in inventory
     *
     * @param rectangle Rectangle of items to get items contained within.
     *                  Width/Height are in number of items. X/Y are row/column.
     * @param items     Items to get those contained within rectangle from.
     * @return Items contained in rectangle in inventory.
     */
    public static RSItem[] getItemsInRectangle(final Rectangle rectangle, final RSItem... items) {
        
        if (items != null
                && rectangle != null
                && rectangle.width >= 1
                && rectangle.height >= 1
                && rectangle.x >= 0
                && rectangle.x <= 6
                && rectangle.y >= 0
                && rectangle.y <= 3) {
            List<RSItem> itemList = new ArrayList<>();

            for (RSItem item : items) {
                int r = getItemRow(item);
                int col = getItemColumn(item);
                if (r >= rectangle.x
                        && r <= rectangle.x + rectangle.width - 1
                        && col >= rectangle.y
                        && col <= rectangle.y + rectangle.height - 1) {
                    itemList.add(item);
                }
            }
            return itemList.toArray(new RSItem[itemList.size()]);
        }
        
        return new RSItem[0];
    }
}
