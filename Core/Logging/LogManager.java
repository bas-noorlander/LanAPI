package scripts.LanAPI.Core.Logging;

import scripts.LanAPI.Core.Logging.Loggers.ClientDebugLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Takes care of all the logging
 *
 * Do not use this directly, use LogProxy instead.
 *
 * @author Laniax
 */
public class LogManager {

    private static final Object _lock = new Object();

    private static final StringBuilder _builder = new StringBuilder();

    public static List<ILogger> _loggers = new ArrayList<>(Arrays.asList(
            //TODO: make this more customizable
            //new BotDebugLogger(),
            new ClientDebugLogger()
    ));

    public static void addLogger(ILogger logger) {
        _loggers.add(logger);
    }

    private static String prepare(String source, String message, Object... args) {

        _builder.append("[");
        _builder.append(source);
        _builder.append("] ");
        _builder.append(String.format(message, args));

        String result =_builder.toString();
        _builder.setLength(0);

        return result;
    }

    static void information(String source, String message, Object... args) {
        synchronized (_lock) {
            for(ILogger log : _loggers) {

                String output = prepare(source, message, args);
                log.writeInformation(output);
            }
        }
    }

    static void warning(String source, String message, Object... args) {
        synchronized (_lock) {
            for(ILogger log : _loggers) {

                String output = prepare(source, message, args);
                log.writeWarning(output);
            }
        }
    }

    static void error(String source, String message, Object... args) {
        synchronized (_lock) {
            for(ILogger log : _loggers) {
                String output = prepare(source, message, args);
                log.writeError(output);
            }
        }
    }

    static void debug(String source, String message, Object... args) {

        synchronized (_lock) {
            for(ILogger log : _loggers) {
                String output = prepare(source, message, args);
                log.writeDebug(output);
            }
        }
    }

}
