package scripts.lanapi.network;

import scripts.lanapi.core.dynamic.Bag;
import scripts.lanapi.core.io.json.Json;
import scripts.lanapi.core.io.json.JsonObject;
import scripts.lanapi.core.io.json.JsonValue;
import scripts.lanapi.network.exceptions.ItemPriceNotFoundException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * @author Laniax
 */
public class ItemPrice {

    private static Bag cache = new Bag();

    private final static int ITEMID_COINS = 995;

    private final static HashMap<Integer, ItemPriceNotFoundException> map = new HashMap<>();

    public static int get(final int itemId) throws ItemPriceNotFoundException {

        Integer cachedPrice = cache.get(String.valueOf(itemId));
        if (cachedPrice != null)
            return cachedPrice;

        // Coins don't have a sell price ;) but lets count them anyway
        if (itemId == ITEMID_COINS)
            return 1;

        // If rsbuddy is down, we dont want to keep requesting all the time.
        // So lets save the items that error'd in a hashmap, and rethrow the error without doing another request.
        if (map.containsKey(itemId)) {
            throw map.get(itemId);
        }

        try {
            URL url = new URL("https://api.rsbuddy.com/grandExchange?a=guidePrice&i=" + itemId);
            try (InputStreamReader reader = new InputStreamReader(url.openStream())) {

                JsonObject response = Json.parse(reader).asObject();

                int price = response.get("overall").asInt();

                if (price == 0) {
                    // Price is 0, there is a chance this item is not tradeable.
                    // throw an error if all the other values are null as well.
                    if (response.get("buying").asInt() == 0 && response.get("buyingQuantity").asInt() == 0 && response.get("selling").asInt() == 0 && response.get("sellingQuantity").asInt() == 0) {
                        ItemPriceNotFoundException ex = new ItemPriceNotFoundException(itemId, "Item is not tradeable");
                        map.put(itemId, ex);
                        throw ex;
                    }
                }

                cache.addOrUpdate(String.valueOf(itemId), price);

                return price;
            } catch (IOException e) {
                ItemPriceNotFoundException ex = new ItemPriceNotFoundException(itemId, e.getMessage());
                map.put(itemId, ex);
                throw ex;
            }
        } catch (MalformedURLException e) {
            ItemPriceNotFoundException ex = new ItemPriceNotFoundException(itemId, e.getMessage());
            map.put(itemId, ex);
            throw ex;
        }
    }
}
