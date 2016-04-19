package scripts.lanapi.game.script;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Login;
import org.tribot.api2007.MessageListener;
import org.tribot.api2007.Skills;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.util.ThreadSettings;
import org.tribot.script.Script;
import org.tribot.script.interfaces.*;
import scripts.lanapi.core.gui.GUI;
import scripts.lanapi.game.antiban.Antiban;
import scripts.lanapi.core.logging.LogProxy;
import scripts.lanapi.core.system.Notifications;
import scripts.lanapi.game.concurrency.IStrategy;
import scripts.lanapi.game.concurrency.observers.inventory.InventoryListener;
import scripts.lanapi.game.concurrency.StrategyList;
import scripts.lanapi.game.painting.AbstractPaintInfo;
import scripts.lanapi.game.painting.PaintHelper;
import scripts.lanapi.game.persistance.Vars;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractScript extends Script implements Painting, MouseActions, MousePainting, MouseSplinePainting,
        EventBlockingOverride, Ending, Breaking, MessageListening07, InventoryListener {

    public boolean quitting = false;

    protected boolean hasArguments = false;
    protected LogProxy log;
    protected boolean waitForGUI = true;
    protected boolean showPaint = false;
    protected GUI gui = null;
    protected BufferedImage icon = null;
    protected AbstractPaintInfo paintInfo = null;
    protected Color mouseColor = null;

    public AbstractScript() {
        Vars.get().add("script", this);

        this.paintInfo = getPaintInfo();
        this.log = new LogProxy(this);

        if (paintInfo != null)
            this.mouseColor = paintInfo.getPrimaryColor();
    }

    //Pattern skillupRegex = Pattern.compile("(?<=\\ba\\s)(\\w+).*\\b([0-9]{1,2})");
    Pattern skillupRegex = Pattern.compile("(?<=\\ba\\s)\\w+");

    /**
     * This method is called once when the script starts and we are logged ingame, just before the paint/gui shows.
     */
    public abstract void onInitialize();

    /**
     * Return a JavaFX gui, it will automatically be shown and the script will wait until it closes.
     * Return null if you don't want a GUI.
     * @return
     */
    public abstract GUI getGUI();

    /**
     * Return a list of all the strategies this script should perform.
     * @return
     */
    public abstract IStrategy[] getStrategies();

    /**
     * Return the notification icon for in the system tray bar.
     * Return null if you don't want an icon.
     * @return
     */
    public abstract BufferedImage getNotificationIcon();

    /**
     * Return this script's paint logic.
     * Return null if you don't want a paint.
     * @return
     */
    public abstract AbstractPaintInfo getPaintInfo();

    @Override
    public void run() {

        log.debug("Hey, thanks for trying LanAPI! These debug messages won't show if you upload this script to the repository.");
        log.debug("You can use log.debug() to write messages like this. Or log.info() to write a normal message!");

        General.useAntiBanCompliance(true);

        ThreadSettings.get().setClickingAPIUseDynamic(true);

        while (Login.getLoginState() != Login.STATE.INGAME) {
            sleep(250);
            Login.login();
        }

        onInitialize();

        icon = getNotificationIcon();
        gui = getGUI();
        showPaint = true;

        if (!hasArguments && gui != null) {

            gui.show();

            while (gui.isOpen())
                sleep(250);
        }

        boolean useNotifications = Vars.get().get("enableNotifications", false);

        if (icon != null && useNotifications) {
            if (Notifications.init(this))
                MessageListener.addListener(this);
        }

        StrategyList list = new StrategyList(getStrategies());

        Antiban.setWaitingSince();

        while (!quitting) {

            while (Login.getLoginState() != Login.STATE.INGAME) {
                sleep(250);
                Login.login();
            }

            IStrategy strategy = list.getValid();
            if (strategy != null) {
                strategy.run();
            }

            sleep(50);
        }
    }

    public boolean isLocal() {
        return this.getRepoID() == -1;
    }

    @Override
    public void onPaint(Graphics g1) {

        if (paintInfo == null)
            return;

        Graphics2D g = (Graphics2D) g1;

        if (showPaint) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            paintInfo.draw(g);
        }
    }

    @Override
    public void onEnd() {

        log.info("Thank you for using %s! You ran this script for %s. If you enjoyed this script, please leave a message on the forums :)", this.getScriptName(), Timing.msToString(this.getRunningTime()));

        Notifications.destroy();

        if (gui != null)
            gui.close();
    }

    @Override
    public void onBreakStart(long breakTime) {

        if (Notifications.getPreferences().isOnBreakStart())
            Notifications.send(this.getScriptName(), "Taking a break for " + Timing.msToString(breakTime), TrayIcon.MessageType.INFO);
    }

    @Override
    public void onBreakEnd() {

        if (Notifications.getPreferences().isOnBreakEnd())
            Notifications.send(this.getScriptName(), "Break Ended", TrayIcon.MessageType.INFO);
    }

    @Override
    public void serverMessageReceived(String s) {

        if (Notifications.getPreferences().isOnSkillLevelUp()) {

            if (s.contains("Congratulations, you just advanced")) {

                Matcher match = skillupRegex.matcher(s);

                if (match.find()) {
                    String skillName = match.group(0);
                    Skills.SKILLS skill = Skills.SKILLS.valueOf(skillName);
                    if (skill != null) {
                        int newLevel = skill.getActualLevel();
                        Notifications.send(String.format("%s level up!", skillName), String.format("Your %s advanced to level %d!", skillName, newLevel), TrayIcon.MessageType.INFO);
                        log.info("You gained a level in %s! You are now %d.", skillName, newLevel);
                    }
                }
            }
        }

        if (Notifications.getPreferences().isOnServerMessage())
            Notifications.send("[Server Message]", s, TrayIcon.MessageType.WARNING);

    }

    @Override
    public void clanMessageReceived(String name, String msg) {

        if (Notifications.getPreferences().isOnClanMessage())
            Notifications.send(String.format("[Clan] %s:", name), msg);
    }

    @Override
    public void playerMessageReceived(String name, String msg) {

        if (Notifications.getPreferences().isOnChatMessage())
            Notifications.send(String.format("[Chat] %s:", name), msg);

    }

    @Override
    public void personalMessageReceived(String name, String msg) {

        if (Notifications.getPreferences().isOnPrivateMessage())
            Notifications.send(String.format("[PM] %s:", name), msg, TrayIcon.MessageType.WARNING);

    }

    @Override
    public void duelRequestReceived(String name, String msg) {

        if (Notifications.getPreferences().isOnDuelRequest())
            Notifications.send("[Duel Request]", String.format("From '%s'", name), TrayIcon.MessageType.WARNING);
    }

    @Override
    public void tradeRequestReceived(String s) {

        if (Notifications.getPreferences().isOnTradeRequest())
            Notifications.send("[Trade Request]", String.format("From '%s'", s), TrayIcon.MessageType.WARNING);

    }

    @Override
    public void paintMouse(Graphics g, Point mousePos, Point dragPos) {
        PaintHelper.drawMouse(g, mousePos, dragPos, mouseColor);
    }

    @Override
    public void mouseClicked(Point p, int button, boolean isBot) {
        PaintHelper.mouseDown = true;
    }

    @Override
    public void paintMouseSpline(Graphics g, ArrayList<Point> points) {
        PaintHelper.drawMouseTrail(g, points, mouseColor);
    }

    @Override
    public void mouseMoved(Point point, boolean isBot) {
        PaintHelper.moveMouseTrail(point);
    }

    // Unused overrides, feel free to override these in your script if you need them.
    public OVERRIDE_RETURN overrideMouseEvent(MouseEvent e) {
        return OVERRIDE_RETURN.PROCESS;
    }
    public void mouseReleased(Point point, int button, boolean isBot) {}
    public void mouseDragged(Point point, int button, boolean isBot) {}
    public OVERRIDE_RETURN overrideKeyEvent(KeyEvent e) {
        return OVERRIDE_RETURN.SEND;
    }
    public void inventoryItemRemoved(RSItem item, int count) {}
    public void inventoryItemAdded(RSItem item, int count) {}
}
