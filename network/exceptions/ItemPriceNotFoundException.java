package scripts.lanapi.network.exceptions;

/**
 * @author Laniax
 */
public class ItemPriceNotFoundException extends Exception {

    private final int itemId;

    public ItemPriceNotFoundException(int itemId) {
        super();
        this.itemId = itemId;
    }

    public ItemPriceNotFoundException(int itemId, String msg) {
        super(msg);
        this.itemId = itemId;
    }

    public int getItemId() {
        return itemId;
    }
}
