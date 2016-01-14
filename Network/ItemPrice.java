package scripts.LanAPI.Network;

import scripts.LanAPI.Core.IO.JSON.JsonObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Laniax
 */
public class ItemPrice {

    public static int get(final int itemId) {
        try {
            URL url = new URL("https://api.rsbuddy.com/grandExchange?a=guidePrice&i=" + itemId);
            try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
                return JsonObject.readFrom(reader).get("overall").asInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
