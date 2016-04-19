package scripts.lanapi.core.logging.loggers;

import org.tribot.api.General;
import scripts.lanapi.core.logging.ILogger;

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
