package scripts.lanapi.game.concurrency;

/**
 * @author Laniax
 */
public interface IStrategy {

    boolean isValid();

    void run();

    int priority();

}
