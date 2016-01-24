package scripts.LanAPI.Core.Filters;

import org.tribot.api.types.generic.Filter;
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
}
