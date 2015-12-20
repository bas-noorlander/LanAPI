package scripts.LanAPI.Game.GroundItems;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Camera;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItemDefinition;
import scripts.LanAPI.Game.Inventory.Inventory;
import scripts.LanAPI.Game.Movement.Movement;
import scripts.LanAPI.Game.Painting.PaintHelper;

/**
 * @author Laniax
 */
public abstract class GroundItems extends org.tribot.api2007.GroundItems {

	/**
	 * Loots items of the ground based on their name.
	 * 
	 * @param name
	 * @return if successfully looted the item, false if otherwise.
	 */
	public static boolean loot(final String name) {
		return loot(name, 0);
	}
	
	/**
	 * Loots items of the ground based on their name.
	 * 
	 * @param name
	 * @param addHeight - adds the height on which the item lies. Required if items lay on tables or similar.
	 * @return if successfully looted the item, false if otherwise.
	 */
	public static boolean loot(final String name, final int addHeight) {
		
		RSGroundItem[] lootItems = findNearest(name);
		if (lootItems.length > 0) 
			return loot(lootItems, addHeight);
		
		return false;
	}

	/**
	 * Loots an array of items from the ground.
	 * 
	 * @param items array
	 * @param addHeight, adds the height on which the item lies. Required if items lay on tables or similar.
	 * @return if successfully looted the item, false if otherwise.
	 */
	public static boolean loot(final RSGroundItem[] items, final int addHeight) {
		
		if (items.length > 0) {
			
			PaintHelper.statusText = "Looting";
			
			for (final RSGroundItem item : items) {

				final RSItemDefinition itemDef = item.getDefinition();
				if (itemDef == null)
					continue;
				
				// If the item stacks and we have one already, we should pick it up even if the inventory is full.
				if (Inventory.isFull() && !(itemDef.isStackable() && Inventory.hasItem(item.getID())))
					return false;

				if (!Movement.canReach(item))
					continue;

				if (!item.isOnScreen())
					Camera.turnToTile(item);
				
				item.setClickHeight(addHeight);

				final int preOwned = Inventory.getCount(item.getID());

				// Apparently just 'Take' would causes issues with multiple items on 1 tile.
				if (itemDef != null && Clicking.click("Take " + itemDef.getName(), item)) {

					return Timing.waitCondition(new Condition() {
						public boolean active() {
							General.sleep(50);
							return preOwned > 0 ? Inventory.getCount(item.getID()) > preOwned : Inventory.find(item.getID()).length > 0;
						}}, General.random(3000, 4000));
				}
			}
		}
		return false;
	}
}
