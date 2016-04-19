package scripts.lanapi.game.concurrency.observers.inventory;

import org.tribot.api.General;
import org.tribot.api2007.Login;
import org.tribot.api2007.types.RSItem;
import scripts.lanapi.game.concurrency.Condition;
import scripts.lanapi.game.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author Laniax
 */
public class InventoryObserver extends Thread {

    private ConcurrentLinkedDeque<InventoryListener> listeners;

    private Condition condition;

    private boolean running = true;

    public InventoryObserver(Condition condition) {
        this.listeners = new ConcurrentLinkedDeque<>();
        this.condition = condition;
    }

    @Override
    public void run() {

        HashMap<Integer, RSItem> oldInventory = createInventoryMap();

        while (running) {

            while (Login.getLoginState() != Login.STATE.INGAME)
                General.sleep(500);

            if (!condition.active()) {
                oldInventory = createInventoryMap();
                continue;
            }

            HashMap<Integer, RSItem> newInventory = createInventoryMap();

            for (Map.Entry<Integer, RSItem> item : newInventory.entrySet()) {

                int oldCount = 0;
                RSItem existingItem = oldInventory.get(item.getKey());
                if (existingItem != null)
                    oldCount = existingItem.getStack();

                int newCount = 0;
                RSItem newItem = item.getValue();
                if (newItem != null)
                    newCount = newItem.getStack();

                if (newCount > oldCount) {
                    triggerItemAdded(newItem, newCount - oldCount);
                } else if (oldCount > newCount) {
                    triggerItemRemoved(newItem, oldCount - newCount);
                }

                oldInventory.remove(item.getKey());
            }

            for (Map.Entry<Integer, RSItem> entry : oldInventory.entrySet())
                if (!newInventory.containsKey(entry.getKey()))
                    triggerItemRemoved(entry.getValue(), entry.getValue().getStack());

            oldInventory = newInventory;

            General.sleep(150);
        }
    }

    public void end() {
        running = false;
    }

    public HashMap<Integer, RSItem> createInventoryMap() {

        HashMap<Integer, RSItem> result = new HashMap<>();

        for (RSItem item : Inventory.getAll()) {
            result.put(item.getID(), item);
        }

        return result;
    }

    public void addListener(InventoryListener inventoryListener) {
        listeners.add(inventoryListener);
    }

    public void triggerItemAdded(RSItem item, int count) {
        for (InventoryListener listener : listeners)
            listener.inventoryItemAdded(item, count);
    }

    public void triggerItemRemoved(RSItem item, int count) {
        for (InventoryListener listener : listeners)
            listener.inventoryItemRemoved(item, count);
    }

}
