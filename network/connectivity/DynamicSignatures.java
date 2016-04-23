package scripts.lanapi.network.connectivity;

import java.util.HashMap;

/**
 * @author Laniax
 */
public interface DynamicSignatures {

    /**
     * The url of the server. In the following format:
     * http://yourdomain.com/scripts/yourscriptname/
     *
     * Leave null if you dont want to use dynamic signatures.
     * @return
     */
    String signatureServerUrl();

    /**
     * Called every 5 minutes to send data to the server. Please send the values -from the script start- and NOT from since the last call.
     *
     * @return A hashmap with String that equals the a Type name on the server, and the integer value of the variable.
     */
    HashMap<String, Integer> signatureSendData();

}
