package scripts.LanAPI.Network;

import scripts.LanAPI.Core.Dynamic.Bag;
import scripts.LanAPI.Core.IO.JSON.Json;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Laniax
 */
public class ItemPrice {

    private static Bag bag = new Bag();

    private final static int ITEMID_COINS = 995;

    public static int get(final int itemId) {

        Integer cachedPrice = bag.get(String.valueOf(itemId));
        if (cachedPrice != null)
            return cachedPrice;

        // Coins don't have a sell price ;) but lets count them anyway
        if (itemId == ITEMID_COINS)
            return 1;

        try {
            URL url = new URL("https://api.rsbuddy.com/grandExchange?a=guidePrice&i=" + itemId);
            try (InputStreamReader reader = new InputStreamReader(url.openStream())) {

                int price = Json.parse(reader).asObject().get("overall").asInt();

                bag.addOrUpdate(String.valueOf(itemId), price);

                return price;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
