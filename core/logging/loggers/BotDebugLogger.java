package scripts.lanapi.core.logging.loggers;

import scripts.lanapi.core.logging.ILogger;

/**
 * @author Laniax
 */
public class BotDebugLogger implements ILogger {

    @Override
    public void writeInformation(String message) {
        System.out.println(message);
    }

    @Override
    public void writeWarning(String message) {
        System.out.println(message);
    }

    @Override
    public void writeError(String message) {
        System.out.println(message);
    }

    @Override
    public void writeDebug(String message) {
        System.out.println(message);
    }
}
