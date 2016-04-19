package scripts.lanapi.network.connectivity;

import org.tribot.api.General;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Laniax
 */
public class Signature {

    private Signature() {

    }

    /**
     * Static way to send the variables to the server and generate statistics / signature.
     * @param remoteURL
     * @param runtimeInMilliseconds
     * @param args
     * @return
     */
    public static boolean send(String remoteURL, long runtimeInMilliseconds, HashMap<String, Integer> args) {

        try {

            URL url = new URL(remoteURL);

            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            httpConn.setRequestMethod("POST");

            StringBuffer requestParams = new StringBuffer();
            requestParams.append(String.format("username=%s&runtime=%d", URLEncoder.encode(General.getTRiBotUsername(), "UTF-8"), runtimeInMilliseconds));
            for(Map.Entry<String, Integer> a : args.entrySet()) {
                requestParams.append(String.format("&%s=%d", a.getKey(), a.getValue()));
            }
            DataOutputStream wr = new DataOutputStream(httpConn.getOutputStream());
            wr.writeBytes(requestParams.toString());
            wr.flush();
            wr.close();

            return httpConn.getResponseCode() == 200;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

}
