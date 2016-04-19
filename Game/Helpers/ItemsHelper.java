package scripts.lanapi.game.helpers;

import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import scripts.lanapi.core.logging.LogProxy;

/**
 * Helper class that manages RSItem logic.
 *
 * @author Laniax
 */
public class ItemsHelper {

    static LogProxy log = new LogProxy("ItemsHelper");

    public static String getName(final RSItem item) {
        RSItemDefinition definition = item.getDefinition();
        if (definition != null) {
            String definitionName = definition.getName();
            if (definitionName != null && !definitionName.isEmpty()) {
                return definitionName;
            }
        }

        return null;
    }
}
