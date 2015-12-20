package scripts.LanAPI.Core.Logging.Loggers;

import org.tribot.api.General;
import scripts.LanAPI.Core.Logging.ILogger;

/**
 * @author Laniax
 */
public class ClientDebugLogger implements ILogger {

    @Override
    public void writeInformation(String message) {
        General.println(message);
    }

    @Override
    public void writeWarning(String message) {
        General.println(message);
    }

    @Override
    public void writeError(String message) {
        General.println(message);
    }

    @Override
    public void writeDebug(String message) {
        General.println(message);
    }
}
