package scripts.LanAPI.Core.Logging;

import org.tribot.script.Script;

/**
 * A proxy to use in classes for shorter log calls
 *
 * @author Laniax
 */
public final class LogProxy {

    private final String source;
    private final boolean isDebug;

    public LogProxy(String source) {

        this.source = source;
        this.isDebug = false;
    }

    public LogProxy(Script script) {

        this.source = script.getScriptName();
        this.isDebug = script.getRepoID() < 0;
    }

    /**
     * Writes an info string to the designated output.
     *
     * @param message - string to write
     * @param args    - any arguments for string.format
     */
    public void info(String message, Object... args) {
        LogManager.information(this.source, message, args);
    }

    /**
     * Writes an warning string to the designated output.
     *
     * @param message - string to write
     * @param args    - any arguments for string.format
     */
    public void warn(String message, Object... args) {
        LogManager.warning(this.source, message, args);
    }

    /**
     * Writes an error string to the designated output.
     *
     * @param message - string to write
     * @param args    - any arguments for string.format
     */
    public void error(String message, Object... args) {
        LogManager.error(this.source, message, args);
    }

    /**
     * Writes an debug string to the designated output.
     * This string will only show if the script is running locally, not when uploaded to the repository.
     *
     * @param message - string to write
     * @param args    - any arguments for string.format
     */
    public void debug(String message, Object... args) {
        if (this.isDebug)
            LogManager.debug(this.source, message, args);
    }
}
