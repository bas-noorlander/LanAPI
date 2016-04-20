package scripts.lanapi.core.patterns;

/**
 * @author Laniax
 */
public interface IStrategy {

    boolean isValid();

    void run();

    int priority();

}
