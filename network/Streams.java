package scripts.lanapi.network;

import scripts.lanapi.core.logging.LogProxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Laniax
 */
public class Streams {

    static LogProxy log = new LogProxy("Streams");

    public static InputStream getInputStream(String url) {
        try {
            return getInputStream(new URL(url));
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
            log.error(e.getStackTrace().toString());
        }

        return null;
    }

    public static InputStream getInputStream(URL url) {

        try {
            return url.openStream();
        } catch (IOException e) {
            log.error(e.getMessage());
            log.error(e.getStackTrace().toString());
        }

        return null;
    }
}
