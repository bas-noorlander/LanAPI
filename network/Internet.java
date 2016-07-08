package scripts.lanapi.network;


import scripts.lanapi.core.logging.LogProxy;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Laniax
 */
public class Internet extends org.tribot.util.Internet{

    private static LogProxy log = new LogProxy("Internet");

    public static void openWebsite(final String url) {
        try {
            openWebsite(new URI(url));
        } catch (URISyntaxException e) {
            log.error("Error opening website '%s'. Message: %s.", url, e.getMessage());
        }

    }

    public static void openWebsite(final URI url) {
        try {
            Desktop.getDesktop().browse(url);
        } catch (IOException e) {
            log.error("Error opening website '%s'. Message: %s.", url, e.getMessage());
        }
    }

}
