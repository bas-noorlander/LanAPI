package scripts.LanAPI.Game.Combat;

import org.tribot.api.interfaces.Clickable07;
import org.tribot.api2007.ChooseOption;

/**
 * @author Laniax
 */
public class Hovering {

    private static Clickable07 hoverTarget;
    private static boolean menuOpen;

    /**
     * Gets the entity that should be mouse-hovered.
     * @return
     */
    public static Clickable07 getEntity() {
        return hoverTarget;
    }

    /**
     * Sets the entity that should be mouse-hovered.
     * @param entity
     */
    public static void setEntity(Clickable07 entity) {
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
    }


}
