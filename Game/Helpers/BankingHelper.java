package scripts.LanAPI.Game.Helpers;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSItem;
import scripts.LanAPI.Game.Concurrency.Condition;
import scripts.LanAPI.Game.Inventory.Inventory;

/**
 * @author Laniax
 */
public class BankingHelper { // Sadly, tribot's Banking class is declared final and cannot be extended.

    private static final int BANKING_INTERFACE = 12;
    private static final int SELECTED_TEXTURE = 813;
    private static final int AMOUNT_INTERFACE = 5;

    private static Condition bankCondition = new Condition() {
        @Override
        public boolean active() {
            General.sleep(50, 100);
            return isBankItemsLoaded();
        }
    };

    public enum Widgets {
        SWAP(16, "Swap"), INSERT(18, "Insert"), ITEM(21, "Item"), NOTE(23, "Note");

        String name;
        int index;

        private Widgets(final int index, final String name) {
            this.index = index;
            this.name = name;
        }
    }

    /**
     * @param widg The widget to check (swap/insert/item/note)
     * @return true if the widget is selected (in red)
     */
    public static boolean isSelected(final Widgets widg) {

        if (!Banking.isBankScreenOpen() || Interfaces.get(BANKING_INTERFACE) == null)
            return false;

        final RSInterfaceChild itemWidget = Interfaces.get(BANKING_INTERFACE, widg.index);

        if (itemWidget != null) {
            return itemWidget.getTextureID() == SELECTED_TEXTURE;
        }
        return false;
    }

    /**
     * Withdraws item(s) from the bank (not noted) and wait until we have it in our inventory
     *
     * @param item
     * @param count
     * @return true if item in inventory, false if unsuccessful.
     */
    public static boolean withdrawItem(RSItem item, int count) {

        return withdrawItem(item, count, false);
    }

    /**
     * Withdraws item(s) from the bank and wait until we have it in our inventory
     *
     * @param item
     * @param count
     * @param noted
     * @return true if item in inventory, false if unsuccessful.
     */
    public static boolean withdrawItem(RSItem item, int count, boolean noted) {

        Widgets widget = noted ? Widgets.NOTE : Widgets.ITEM;

        if (select(widget)) {

            final int preAmount = Inventory.getCount(item);

            if (Banking.withdrawItem(item, count)) {

                return Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        General.sleep(50, 100);
                        return Inventory.getCount(item) == (preAmount + count);
                    }
                }, General.random(2500, 3500));
            }
        }

        return false;
    }

    /**
     * @param widg The widget to enable
     * @return true if the widget was successfully selected
     */
    public static boolean select(final Widgets widg) {

        if (!Banking.isBankScreenOpen() || Interfaces.get(BANKING_INTERFACE) == null)
            return false;

        if (isSelected(widg))
            return true;

        final RSInterfaceChild itemWidget = Interfaces.get(BANKING_INTERFACE, widg.index);

        if (itemWidget != null) {
            if (itemWidget.click(widg.name)) {
                return (Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        General.sleep(50);
                        return isSelected(widg);
                    }
                }, General.random(3000, 5000)));
            }
        }
        return false;
    }

    /**
     * Waits until the bank items are fully loaded
     *
     * @return true if they are fully loaded, false is timeout was reached.
     */
    public static boolean waitUntilBankItemsLoaded() {
        return Timing.waitCondition(bankCondition, General.random(3000, 4000));
    }

    public static boolean isBankItemsLoaded() {
        return getCurrentBankSpace() == Banking.getAll().length;
    }

    private static int getCurrentBankSpace() {
        RSInterface amount = Interfaces.get(BANKING_INTERFACE, AMOUNT_INTERFACE);
        if (amount != null) {
            String text = amount.getText();
            if (text != null) {
                try {
                    int parse = Integer.parseInt(text);
                    if (parse > 0)
                        return parse;
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }
        return -1;
    }
}
