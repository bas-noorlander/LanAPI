package scripts.lanapi.network.connectivity;

import org.tribot.api.General;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Laniax
 */
public class Signature {

    private static Signature instance;

    private boolean sessionStarted = false;

    private String token;
    private String url;
    private final String newSessionUrl = "session/new";
    private final String updateSessionUrl = "session/update";
    private final String signatureUrl = "signatures/";

    private Signature() {
        // prevent direct instantiation
    }

    public static Signature get() {

        if (instance == null)
            instance = new Signature();

        return instance;
    }

    public int getTimeout() {
        return 4;
    }

    public boolean hasSession() {
        return this.sessionStarted;
    }

    /**
     * Request a new session from the server.
     * Sets the token we will use this session if successful.
     * @return
     */
    public synchronized boolean startSession() {

        if (this.hasSession())
            return false;

        try {
            URL url = new URL(this.getUrl() + this.newSessionUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod("POST");

            String params = String.format("username=%s", URLEncoder.encode(General.getTRiBotUsername(), "UTF-8"));

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(params);
            wr.flush();
            wr.close();

            if (con.getResponseCode() != 200)
                return false;

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            this.token = in.readLine();
            in.close();

            return this.sessionStarted = true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public synchronized boolean send(DynamicSignatures script) {

        if (!this.sessionStarted)
            return false;

        HashMap<String, Integer> data = script.signatureSendData();

        if (data == null || data.isEmpty())
            return false;

        try {
            URL url = new URL(this.getUrl() + this.updateSessionUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod("POST");

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("token=%s", this.token));

            for(Map.Entry<String, Integer> a : data.entrySet()) {
                sb.append(String.format("&%s=%d", a.getKey(), a.getValue()));
            }

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(sb.toString());
            wr.flush();
            wr.close();

            return con.getResponseCode() == 200;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Gets the URL where the current user's signature is.
     * Example output: http://yourdomain.com/scripts/yourscriptname/signatures/user.png
     * @return
     */
    public String getImageUrl() {
        try {
            return this.url + this.signatureUrl + URLEncoder.encode(General.getTRiBotUsername(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
