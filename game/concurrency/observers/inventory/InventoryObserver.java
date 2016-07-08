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

        HashMap<Integer, RSItem> old_inventory = createInventoryMap();

        while (running) {

            while (Login.getLoginState() != Login.STATE.INGAME)
                General.sleep(500);

            if (!condition.active()) {
                old_inventory = createInventoryMap();
                continue;
            }

            HashMap<Integer, RSItem> new_inventory = createInventoryMap();

            for (Map.Entry<Integer, RSItem> item : new_inventory.entrySet()) {

                int old_count = 0;
                RSItem existing_item = old_inventory.get(item.getKey());
                if (existing_item != null)
                    old_count = existing_item.getStack();

                int new_count = 0;
                RSItem new_item = item.getValue();
                if (new_item != null)
                    new_count = new_item.getStack();

                if (new_count > old_count) {
                    triggerItemAdded(new_item, new_count - old_count);
                } else if (old_count > new_count) {
                    triggerItemRemoved(new_item, old_count - new_count);
                }

                old_inventory.remove(item.getKey());
            }

            for (Map.Entry<Integer, RSItem> entry : old_inventory.entrySet())
                if (!new_inventory.containsKey(entry.getKey()))
                    triggerItemRemoved(entry.getValue(), entry.getValue().getStack());

            old_inventory = new_inventory;

            try {
                sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
