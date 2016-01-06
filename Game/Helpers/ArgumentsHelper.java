package scripts.LanAPI.Game.Helpers;

import scripts.LanAPI.Core.Logging.LogProxy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Laniax
 */
public class ArgumentsHelper {

    static LogProxy log = new LogProxy("ArgumentsHelper");

    /**
     * Converts the raw hashmap received in onEnd to a hashmap with useful key/values
     * @param input
     * @return
     */
    public static HashMap<String, String> get(HashMap<String, String> input) {

        String scriptSelect = input.get("custom_input");

        HashMap<String, String> args = new HashMap<>();

        if (scriptSelect != null) {
            // Trim whitespace
            scriptSelect = scriptSelect.replaceAll("\\s","");

            // Split everything by comma
            List<String> argList = Arrays.asList(scriptSelect.split(","));

            for(String arg : argList) {

                if (!arg.contains(":")) {
                    log.error("Found invalid argument '%s', skipping.", arg);
                    continue;
                }

                String[] tmp = arg.split(":");
                args.put(tmp[0], tmp[1]);
            }

            for (Map.Entry<String, String> entry : args.entrySet()) {
                log.info("Found argument '%s' with value '%s'", entry.getKey(), entry.getValue());
            }
        }

        return args;
    }
}
