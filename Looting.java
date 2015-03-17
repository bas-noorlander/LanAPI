package scripts.LanAPI;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Camera;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItemDefinition;

/**
 * @author Laniax
 *
 */
public class Looting {

	/**
	 * Loots items of the ground based on their name.
	 * 
	 * @param name
	 * @return if successfully looted the item, false if otherwise.
	 */
	public static boolean lootGroundItem(final String name) {
		return lootGroundItem(name, 0);
	}
	
	/**
	 * Loots items of the ground based on their name.
	 * 
	 * @param name
	 * @param addHeight - adds the height on which the item lies. Required if items lay on tables or similar.
	 * @return if successfully looted the item, false if otherwise.
	 */
	public static boolean lootGroundItem(final String name, final int addHeight) {
		
		RSGroundItem[] lootItems = GroundItems.findNearest(name);
		if (lootItems.length > 0) 
			return lootGroundItems(lootItems, addHeight);
		
		return false;
	}

	/**
	 * Loots an array of items from the ground.
	 * 
	 * @param items array
	 * @param addHeight, adds the height on which the item lies. Required if items lay on tables or similar.
	 * @return if successfully looted the item, false if otherwise.
	 */
	public static boolean lootGroundItems(final RSGroundItem[] items, final int addHeight) {
		
		if (items.length > 0) {
			
			for (final RSGroundItem item : items) {
				
				// If the item stacks and we have one already, we should pick it up even if the inventory is full.
				if (Inventory.isFull() && (item.getStack() == 1 || Inventory.find(item.getID()).length == 0))
					return false;
				
				if (!Movement.canReach(item))
					continue;

				if (!item.isOnScreen())
					Camera.turnToTile(item);
				
				item.setClickHeight(addHeight);

				final int preOwned = Inventory.getCount(item.getID());
				final RSItemDefinition itemDef = item.getDefinition();

				// Apparently just 'Take' would causes issues with multiple items on 1 tile.
				if (itemDef != null && DynamicClicking.clickRSGroundItem(item,"Take "+ itemDef.getName())) {

					return Timing.waitCondition(new Condition() {
						public boolean active() {
							General.sleep(50);
							return preOwned > 0 ? Inventory.getCount(item.getID()) > preOwned : Inventory.find(item.getID()).length > 0;
						}}, General.random(1000, 2000));
				}
			}
		}
		return false;
	}
}
