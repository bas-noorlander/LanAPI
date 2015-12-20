package scripts.LanAPI.Game.Concurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Laniax
 */
public class StrategyList extends ArrayList<IStrategy> {

    public StrategyList(IStrategy... strategies) {
        super(Arrays.asList(strategies));

        Collections.sort(this, (o1, o2) ->  o2.priority() - o1.priority());
    }

    public IStrategy getValid() {

        return this.stream().filter(strategy -> strategy.isValid()).findFirst().orElse(null);
    }

}
