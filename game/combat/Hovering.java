package scripts.lanapi.game.combat;

import org.tribot.api.interfaces.Clickable;
import org.tribot.api2007.ChooseOption;

/**
 * @author Laniax
 */
public class Hovering {

    private static Clickable hoverTarget;
    private static boolean menuOpen;
    private static Clickable hoverItem;

    /**
     * Gets the entity that should be mouse-hovered.
     * @return
     */
    public static Clickable getEntity() {
        return hoverTarget;
    }

    /**
     * Sets the entity that should be mouse-hovered.
     * @param entity
     */
    public static void setEntity(Clickable entity) {
        hoverTarget = entity;
    }

    /**
     * Sets if we should open the menu when hovering.
     * @param value
     */
    public static void setShouldOpenMenu(boolean value) {
        menuOpen = value;
    }

    /**
     * Gets if we should open the menu when hovering.
     * @return
     */
    public static boolean getShouldOpenMenu() {
        return menuOpen;
    }

    /**
     * Stop the mouse from hovering.
     */
    public static void stop() {
        hoverTarget = null;
    }

    /**
     * Gets if we (should) be hovering a entity right now.
     * @return
     */
    public static boolean isHovering() {
        return getEntity() != null;
    }

    /**
     * Returns if the right click menu is open and we should be hovering.
     * @return
     */
    public static boolean isMenuOpen() {
        return isHovering() && ChooseOption.isOpen();
    }

    public static void reset() {
        hoverTarget = null;
        menuOpen = false;
        hoverItem = null;
    }

    /**
     * Gets the menu node which we should be hovering right now
     * @return
     */
    public static Clickable getHoveringItem() {
        return hoverItem;
    }

    /**
     * Sets the menu node which we are hovering right now.
     * *Note* this is purely a 'tracker' it doesn't actually start hovering the node.
     * @param value
     */
    public static void setHoveringItem(Clickable value) {
        hoverItem = value;
    }
}
