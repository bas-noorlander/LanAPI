package scripts.LanAPI.Core.Filters;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItem;

/**
 * @author Laniax
 */
public class Filters extends org.tribot.api2007.ext.Filters {

    public static class Items extends org.tribot.api2007.ext.Filters.Items {

        /**
         * Generates a filter to see if an {@link RSItem} is in the given array.
         * @param items
         * @return
         */
        public static Filter<RSItem> itemEquals(final RSItem... items) {

            return new Filter<RSItem>() {
                @Override
                public boolean accept(RSItem rsItem) {

                    for (RSItem item : items) {
                        if (rsItem.equals(item))
                            return true;
                    }
                    return false;
                }
            };

        }

    }

    public static class GroundItems extends org.tribot.api2007.ext.Filters.GroundItems {

        /**
         * Generates a filter to see if an (@link RSGroundItem) is in the given area.
         * @param area
         * @return
         */
        public static Filter<RSGroundItem> inArea(final RSArea area) {

            return new Filter<RSGroundItem>() {
                @Override
                public boolean accept(RSGroundItem rsGroundItem) {
                    return area.contains(rsGroundItem);
                }
            };

        }
    }
}
