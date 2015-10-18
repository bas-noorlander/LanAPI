package scripts.LanAPI;

/**
 * @author Laniax
 */
public class Inventory extends org.tribot.api2007.Inventory {
	
	/**
	 * Returns if the inventory is empty or not.
	 * @return true if the inventory is empty, false otherwise.
	 */
	public static boolean isEmpty() {
		
		return Inventory.getAll().length == 0;
	}
	
	/**
	 * Returns how many free spaces the player has in its inventory.
	 * @return the amount of free spaces
	 */
	public static int getAmountOfFreeSpace() {
		
		return 28 - Inventory.getAll().length;
	}
	
	/**
	 * Returns if we have one of the item IDs in our inventory
	 * @param itemIDs
	 * @return true if we have one of the items, false otherwise.
	 */
	public static boolean hasItem(final int... itemIDs) {
		
		return Inventory.find(itemIDs).length > 0;
	}
}