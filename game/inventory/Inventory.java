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
				if ((col < lowestCol && col >= 0) || lowestCol == -1) {
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
				if ((r < lowestRow && r >= 0) || lowestRow == -1) {
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
	public static RSItem[] getItemsInLowestRectangle(int width, int height, final RSItem... items) {
	    
		return getItemsInRectangle(getLowestContainingRow(items), getLowestContainingColumn(items), width, height, items);
	}

	/**
	 * Gets given items contained within given rectangle of items in inventory
	 *
	 * @param baseRow Start row to get items from
	 * @param baseCol Start column to get items from
	 * @param width   Width of area in number of items to get from.
	 * @param height  Height of area in number of items to get from.
	 * @param items   Items to get those contained within rectangle from.
	 * @return Items contained in rectangle in inventory.
	 */
	public static RSItem[] getItemsInRectangle(final int baseRow, final int baseCol, final int width, final int height, final RSItem... items) {
		
		if (items != null
				&& width >= 1
				&& height >= 1
				&& baseRow >= 0
				&& baseRow <= 6
				&& baseCol >= 0
				&& baseCol <= 3) {
			List<RSItem> itemList = new ArrayList<>();
			for (RSItem item : items) {
				int r = getItemRow(item);
				int col = getItemColumn(item);
				if (r >= baseRow
						&& r <= baseRow + height - 1
						&& col >= baseCol
						&& col <= baseCol + width - 1) {
					itemList.add(item);
				}
			}
			
			return itemList.toArray(new RSItem[itemList.size()]);
		}
		
		return new RSItem[0];
	}

	/**
	 * Gets items in lowest containing square of items of given width and height.
	 *
	 * @param width Width of square.
	 * @param items Items to get those contained within rectangle from.
	 * @return Items contained in lowest containing rectangle of items with given width/height in inventory.
	 */
	public static RSItem[] getItemsInLowestSquare(int width, final RSItem... items) {
	    
		return getItemsInSquare(getLowestContainingRow(items), getLowestContainingColumn(items), width, items);
	}

	/**
	 * Gets given items contained within given square of items in inventory
	 *
	 * @param baseRow Start row to get items from
	 * @param baseCol Start column to get items from
	 * @param width   Width of area in number of items to get from.
	 * @param items   Items to get those contained within rectangle from.
	 * @return Items contained in rectangle in inventory.
	 */
	public static RSItem[] getItemsInSquare(final int baseRow, final int baseCol, final int width, final RSItem... items) {
	    
		return getItemsInRectangle(baseRow, baseCol, width, width, items);
	}

	/**
	 * Gets the items that surround the given item.
	 * Uses the actual given item not just generically make sure its in the inventory.
	 *
	 * @param rsItem           item to get surrounding items of.
	 * @param surroundingWidth Width in number of items of the surrounding items.
	 *                         For example 1 to get a 1 item thick set of surrounding items
	 *                         like
	 *                         ***
	 *                         *i*
	 *                         ***
	 *                         or 2 to get a 2 thick set of surrounding items
	 *                         like
	 *                         *****
	 *                         *****
	 *                         **i**
	 *                         *****
	 *                         *****
	 *                         and so on.
	 * @return the items surrounding the given item in the inventory.
	 */
	public static RSItem[] getSurroundingItems(final RSItem rsItem, int surroundingWidth) {
	    
		if (rsItem != null && hasItem(rsItem)) {
		    
			int row = getItemRowUnsafe(rsItem) - surroundingWidth;
			int col = getItemColumnUnsafe(rsItem) - surroundingWidth;
			surroundingWidth = (surroundingWidth * 2) + 1;
			int surroundingHeight = surroundingWidth;
			
			if (row < 0) {
				surroundingHeight += row;
				row = 0;
			}
			if (col < 0) {
				surroundingWidth += col;
				col = 0;
			}
			
			List<RSItem> itemList = new ArrayList<>();
			itemList.addAll(Arrays.asList(getItemsInRectangle(row, col, surroundingWidth, surroundingHeight, getAll())));
			itemList.remove(rsItem);
			
			return itemList.toArray(new RSItem[itemList.size()]);
		}
		
		return new RSItem[0];
	}
}
