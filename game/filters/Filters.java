package scripts.lanapi.game.filters;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.*;

/**
 * @author Laniax
 */
public class Filters extends org.tribot.api2007.ext.Filters {

    public static class Items extends org.tribot.api2007.ext.Filters.Items {

        /**
         * Generates a filter to see if an {@link RSItem} is in the given array.
         *
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

        /**
         * Generates a filter that returns all the items that are noted.
         *
         * @return
         */
        public static Filter<RSItem> isNoted() {

            return new Filter<RSItem>() {
                @Override
                public boolean accept(RSItem rsItem) {
                    RSItemDefinition def = rsItem.getDefinition();
                    return def != null && def.isNoted();
                }
            };
        }
    }

    public static class GroundItems extends org.tribot.api2007.ext.Filters.GroundItems {

        /**
         * Generates a filter to see if an (@link RSGroundItem) is in the given area.
         *
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
         * Generates a filter that will return all the {@link RSNPC}s that are the given combat level.
         *
         * @param level
         * @return
         */
        public static Filter<RSNPC> isCombatLevel(final int level) {

            return new Filter<RSNPC>() {
                @Override
                public boolean accept(RSNPC rsnpc) {
                    return rsnpc.getCombatLevel() == level;
                }
            };
        }

        /**
         * Generates a filter that will return all the {@link RSNPC}s except for the given one.
         *
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
         *
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
         *
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
         * Generates a filter that will return all the {@link RSNPC}s that are valid.
         *
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

        /**
         * Generates a filter that will return all the {@link RSNPC}s that are interacting with the player.
         *
         * @return
         */
        public static Filter<RSNPC> isInteractingWithMe() {

            return new Filter<RSNPC>() {
                @Override
                public boolean accept(RSNPC rsnpc) {
                    return rsnpc.isInteractingWithMe();
                }
            };
        }

        /**
         * Generates a filter that will return all the {@link RSNPC}s that reachable.
         * NOTE: canReach is an expensive call and shouldn't be used lightly!
         *
         * @return
         */
        public static Filter<RSNPC> canReach() {

            return new Filter<RSNPC>() {
                @Override
                public boolean accept(RSNPC rsnpc) {
                    return PathFinding.canReach(Player.getPosition(), rsnpc, false);
                }
            };
        }
    }

    public static class Players extends org.tribot.api2007.ext.Filters.Players {

        /**
         * Generates a filter that will return all {@link RSPlayer}s that are within the radius around the current player's position.
         * @param radius
         * @return
         */
        public static Filter<RSPlayer> withinRadius(final int radius) {

            return withinRadius(Player.getPosition(), radius);
        }

        /**
         * Generates a filter that will return all {@link RSPlayer}s that are within the radius around the given positionable.
         * @param radius
         * @return
         */
        public static Filter<RSPlayer> withinRadius(final Positionable pos, final int radius) {

            return new Filter<RSPlayer>() {

                final RSTile pos_to_check = pos.getPosition();

                @Override
                public boolean accept(RSPlayer player) {
                    return pos_to_check.distanceTo(player) <= radius;
                }
            };
        }

    }

    public static class Projectiles {

        /**
         * Generates a filter that will return all the {@link RSProjectile} that were fired by the given npc.
         * Note this is calculated based on the position of the npc when it fired the projectile.
         * There may be projectiles missing in the result if the NPC moves around a lot.
         *
         * @param npc
         * @return
         */
        public static Filter<RSProjectile> sourceNPC(final RSNPC npc) {

            return new Filter<RSProjectile>() {

                final int plane = Player.getPosition().getPlane();

                @Override
                public boolean accept(RSProjectile projectile) {

                    RSTile tile = new RSTile(projectile.getLocalX(), projectile.getLocalY(), plane, RSTile.TYPES.LOCAL).toWorldTile();

                    return projectile.isTargetingMe() && npc.getPosition().equals(tile);
                }
            };
        }

        /**
         * Generates a filter that will return all the {@link RSProjectile} that were fired by the given object.
         * Note this is calculated based on the position of the object when it fired the projectile.
         * There may be projectiles missing in the result if the object moves around a lot.
         *
         * @param object
         * @return
         */
        public static Filter<RSProjectile> sourceObject(final RSObject object) {

            return new Filter<RSProjectile>() {

                final RSTile obj_position = object.getPosition();

                @Override
                public boolean accept(RSProjectile projectile) {

                    final RSTile projectile_start_pos = new RSTile(projectile.getStartX(), projectile.getStartY(), projectile.getPlane(), RSTile.TYPES.LOCAL).toWorldTile();

                    return projectile_start_pos.equals(obj_position);
                }
            };
        }
    }
}
