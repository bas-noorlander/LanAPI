package scripts.lanapi.core.system;

import scripts.lanapi.core.gui.client.FrameManager;
import scripts.lanapi.core.gui.GUI;
import scripts.lanapi.core.logging.LogProxy;
import scripts.lanapi.game.script.LANScript;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Laniax
 */
public class Notifications {

    static LogProxy log = new LogProxy("Notifications");

    private static boolean _isInitialized = false;
    private static TrayIcon icon;

    private static NotificationPreferences preferences = new NotificationPreferences();

    public static NotificationPreferences getPreferences() {
        return preferences;
    }

    public static void setPreferences(NotificationPreferences value) {
        preferences = value;
    }

    public static boolean init(LANScript script) {

        if (_isInitialized || !SystemTray.isSupported())
            return false;

        BufferedImage scriptIcon = script.getNotificationIcon();

        if (scriptIcon == null)
            return false;

        // Add a context menu to the icon as well.
        PopupMenu menu = new PopupMenu();

        GUI gui = script.getGUI();

        if (gui != null) {
            MenuItem showGUI = new MenuItem("Show Settings");
            showGUI.addActionListener(e -> gui.show());
            menu.add(showGUI);
        }

        MenuItem showTribot = new MenuItem("Show Tribot");
        showTribot.addActionListener(e -> FrameManager.toFront());
        menu.add(showTribot);

        // TrayIcon#setImageAutoSize(true) is aliased and looks really ugly, so we do it ourselves instead!
        int iconWidth = new TrayIcon(scriptIcon).getSize().width;
        icon = new TrayIcon(scriptIcon.getScaledInstance(iconWidth, -1, Image.SCALE_SMOOTH), script.getScriptName(), menu);

        icon.addActionListener(e -> FrameManager.toFront()); // happens when the tray icon is clicked

        try {
            SystemTray.getSystemTray().add(icon);

            log.info("Initialized.");
            _isInitialized = true;
            return true;
        } catch (AWTException e) {
            log.error("Error adding a icon to the system tray menu. Message: %s", e.getMessage());
        }

        return false;
    }

    public static void destroy() {

        if (_isInitialized && icon != null) {
            SystemTray.getSystemTray().remove(icon);
            log.info("Destroyed.");
        }
    }

    public static void send(String title, String message) {

        send(title, message, TrayIcon.MessageType.INFO);

    }

    public static void send(String title, String message, TrayIcon.MessageType type) {

        if (!_isInitialized || icon == null)
            return;

        icon.displayMessage(title, message, type);

    }

}