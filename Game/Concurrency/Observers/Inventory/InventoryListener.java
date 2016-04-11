package scripts.LanAPI.Game.Concurrency.Observers.Inventory;

import org.tribot.api2007.types.RSItem;

/**
 * @author Laniax
 */
public interface InventoryListener {

    void inventoryItemAdded(RSItem item, int count);
    void inventoryItemRemoved(RSItem item, int count);

}
