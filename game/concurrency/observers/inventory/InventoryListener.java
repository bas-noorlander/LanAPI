package scripts.lanapi.game.concurrency.observers.inventory;

import org.tribot.api2007.types.RSItem;

/**
 * @author Laniax
 */
public interface InventoryListener {

    void inventoryItemAdded(RSItem item, int count);
    void inventoryItemRemoved(RSItem item, int count);

}
