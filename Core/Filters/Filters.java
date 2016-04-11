package scripts.LanAPI.Core.Filters;

import org.tribot.api.General;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.*;

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

    public static class NPCs extends org.tribot.api2007.ext.Filters.NPCs {

        /**
         * Generates a filter that will return all the {@link RSNPC}s except for the given one.
         * @param npc
         * @return
         */
        public static Filter<RSNPC> isNot(final RSNPC npc) {

            return new Filter<RSNPC>() {
                @Override
                public boolean accept(RSNPC rsnpc) {
                    return !rsnpc.equals(npc);
                }
            };
        }

        /**
         * Generates a filter that will return all the {@link RSNPC}s that are in combat
         * @return
         */
        public static Filter<RSNPC> inCombat() {

            return new Filter<RSNPC>() {
                @Override
                public boolean accept(RSNPC rsnpc) {
                    return rsnpc.isInCombat();
                }
            };
        }

        /**
         * Generates a filter that will return all the {@link RSNPC}s that are not in combat
         * @return
         */
        public static Filter<RSNPC> notInCombat() {

            return new Filter<RSNPC>() {
                @Override
                public boolean accept(RSNPC rsnpc) {
                    return !rsnpc.isInCombat();
                }
            };
        }

        /**
         * Generates a filter that will return all the {@link RSNPC}s that are valid (not dead/dying).
         * @return
         */
        public static Filter<RSNPC> isValid() {

            return new Filter<RSNPC>() {
                @Override
                public boolean accept(RSNPC rsnpc) {
                    return rsnpc.isValid();
                }
            };
        }
    }

    public static class Projectiles {

        /**
         * Generates a filter that will return all the {@link RSProjectile} that were fired by the given npc.
         * Note this is calculated based on the position of the npc when it fired the projectile.
         * There may be projectiles missing in the result if the NPC moves around a lot.
         * @param npc
         * @return
         */
        public static Filter<RSProjectile> sourceNPC(final RSNPC npc) {

            return new Filter<RSProjectile>() {

                int plane = Player.getPosition().getPlane();

                @Override
                public boolean accept(RSProjectile projectile) {

                    RSTile tile = new RSTile(projectile.getLocalX(), projectile.getLocalY(), plane, RSTile.TYPES.LOCAL).toWorldTile();

                    return projectile.isTargetingMe() && npc.getPosition().equals(tile);
                }
            };
        }
    }
}
