package scripts.LanAPI.Game.Concurrency;

/**
 * @author Laniax
 */
public interface IStrategy {

    boolean isValid();

    void run();

    int priority();

}
