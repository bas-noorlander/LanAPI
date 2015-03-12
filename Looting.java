package scripts.LanAPI;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Camera;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSGroundItem;

/**
 * @author Laniax
 *
 */
public class Looting {

	/**
	 * Loots an item off the ground based on its name.
	 * 
	 * @param id
	 * @return if successfully looted the item, false if otherwise.
	 */
	public static boolean lootGroundItem(final String name) {
		return lootGroundItem(name, 0);
	}

	/**
	 * Loots an item off the ground based on its name.
	 * 
	 * @param id
	 * @param addHeight, adds the height on which the item lies. Required if items lay on tables or similar.
	 * @return if successfully looted the item, false if otherwise.
	 */
	public static boolean lootGroundItem(final String name, final int addHeight) {
		
		RSGroundItem lootItems[] = GroundItems.findNearest(name);
		
		if (lootItems.length > 0) {
			for (final RSGroundItem item : lootItems) {
				if (Inventory.isFull())
					return false;

				Camera.turnToTile(item);
				item.setClickHeight(addHeight);

				final int preOwned = Inventory.getCount(item.getID());

				// Apparently just 'Take' would causes issues with multiple items on 1 tile.
				if (item.click("Take "+ item.getDefinition().getName())) {

					return Timing.waitCondition(new Condition() {
						public boolean active() {
							return preOwned > 0 ? Inventory.getCount(item.getID()) > preOwned : Inventory.find(item.getID()).length > 0;
						}}, General.random(1000, 2000));
				}
			}
		}
		return false;
	}

}
