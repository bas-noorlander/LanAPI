package scripts.lanapi.game.grounditems;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Camera;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItemDefinition;
import scripts.lanapi.core.logging.LogProxy;
import scripts.lanapi.game.inventory.Inventory;
import scripts.lanapi.game.movement.Movement;
import scripts.lanapi.game.painting.PaintHelper;
import scripts.lanapi.game.persistance.Vars;
import scripts.lanapi.game.script.AbstractScript;
import scripts.lanapi.network.ItemPrice;
import scripts.lanapi.network.exceptions.ItemPriceNotFoundException;

/**
 * @author Laniax
 */
public abstract class GroundItems extends org.tribot.api2007.GroundItems {

    private static final LogProxy log = new LogProxy("GroundItems");

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

    public static String getName(RSGroundItem item) {
        RSItemDefinition definition = item.getDefinition();
        if (definition != null) {
            String definitionName = definition.getName();
            if (definitionName != null) {
                return definitionName;
            }
        }

        return null;
    }

    /**
     * Loots an array of items from the ground.
     *
     * @param items      array
     * @param addHeight, adds the height on which the item lies. Required if items lay on tables or similar.
     * @return if successfully looted the item, false if otherwise.
     */
    public static boolean loot(final RSGroundItem[] items, final int addHeight) {

        if (items.length > 0) {

            PaintHelper.status_text = "Looting";

            for (final RSGroundItem item : items) {

                final RSItemDefinition itemDef = item.getDefinition();

                if (itemDef == null)
                    continue;

                // If the item stacks and we have one already, we should pick it up even if the inventory is full.
                if (Inventory.isFull() && !(itemDef.isStackable() && Inventory.hasItem(item.getID())))
                    continue;

                if (!Movement.canReach(item))
                    continue;

                if (!item.isOnScreen())
                    Camera.turnToTile(item);

                item.setClickHeight(addHeight);

                final int preOwned = Inventory.getCount(item.getID());

                // Apparently just 'Take' would causes issues with multiple items on 1 tile.
                if (Clicking.click("Take " + itemDef.getName(), item)) {

                    if (Timing.waitCondition(new Condition() {
                        public boolean active() {
                            General.sleep(50);
                            return preOwned > 0 ? Inventory.getCount(item.getID()) > preOwned : Inventory.find(item.getID()).length > 0;
                        }
                    }, General.random(3000, 4000))) {

                        AbstractScript script = Vars.get().get("script");

                        if (script != null) {

                            // If you have a inventory observer running, make sure to handle profit in there.
                            // Otherwise it gets added here.
                            if (!script.isInventoryObserverRunning()) {
                                try {
                                    PaintHelper.profit += (ItemPrice.get(item.getID()) * item.getStack());
                                } catch (ItemPriceNotFoundException e) {
                                    log.error("Couldn't find value for item id: %s.", item.getID());
                                }
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
