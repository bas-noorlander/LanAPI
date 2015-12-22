package scripts.LanAPI.Network;

import scripts.LanAPI.Core.Logging.LogProxy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

/**
 * @author Laniax
 */
public final class Transmission {

    LogProxy log = new LogProxy("Transmission");

    /**
     * Downloads a string from a text file.
     *
     * @param url - the url to download from
     * @return a String with the downloaded data.
     */
    public static String downloadTextFile(final String url) {

        String result = "";

        try {
            Scanner scanner = new Scanner(new URL(url).openStream(), "UTF-8");
            scanner.useDelimiter("\\A");
            result = scanner.next();
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Download a file from a remote location.
     *
     * @param remote - URL of the remote file
     * @param local  - The file where it should be stored locally.
     * @return true if successful, false otherwise.
     */
    public static boolean download(final String remote, final File local) {
        try {
            URL website = new URL(remote);
            Files.copy(website.openStream(), local.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
