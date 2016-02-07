package scripts.LanAPI.Game.Script;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Login;
import org.tribot.api2007.MessageListener;
import org.tribot.api2007.Skills;
import org.tribot.api2007.util.ThreadSettings;
import org.tribot.script.Script;
import org.tribot.script.interfaces.*;
import scripts.LanAPI.Core.GUI.GUI;
import scripts.LanAPI.Game.Antiban.Antiban;
import scripts.LanAPI.Core.Logging.LogProxy;
import scripts.LanAPI.Core.System.Notifications;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.Concurrency.StrategyList;
import scripts.LanAPI.Game.Painting.AbstractPaintInfo;
import scripts.LanAPI.Game.Painting.PaintHelper;
import scripts.LanAPI.Game.Painting.PaintString;
import scripts.LanAPI.Game.Persistance.Variables;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractScript extends Script implements Painting, MouseActions, MousePainting, MouseSplinePainting, EventBlockingOverride, Ending, Breaking, MessageListening07 {

    public static boolean quitting = false;

    protected boolean hasArguments = false;
    protected LogProxy log;
    protected boolean waitForGUI = true;
    protected boolean showPaint = false;
    protected GUI gui = null;
    protected BufferedImage icon = null;
    protected AbstractPaintInfo paintInfo = null;

    //Pattern skillupRegex = Pattern.compile("(?<=\\ba\\s)(\\w+).*\\b([0-9]{1,2})");
    Pattern skillupRegex = Pattern.compile("(?<=\\ba\\s)\\w+");

    public abstract void onInitialize();

    public abstract GUI getGUI();

    public abstract IStrategy[] getStrategies();

    public abstract BufferedImage getNotificationIcon();

    public abstract AbstractPaintInfo getPaintInfo();

    @Override
    public void run() {

        log = new LogProxy(this);

        log.debug("Hey, thanks for trying LanAPI! These debug messages won't show if you upload this script to the repository.");
        log.debug("You can use log.debug() to write messages like this. Or log.info() to write a normal message!");

        General.useAntiBanCompliance(true);

        ThreadSettings.get().setClickingAPIUseDynamic(true);

//         wait until login bot is done.
        while (Login.getLoginState() != Login.STATE.INGAME) {
            sleep(250);
            Login.login();
        }

        onInitialize();

        icon = getNotificationIcon();
        gui = getGUI();
        paintInfo = getPaintInfo();

        showPaint = true;

        if (!hasArguments && gui != null) {

            gui.show();

            while (gui.isOpen())
                sleep(250);
        }

        boolean useNotifications = Variables.getInstance().get("enableNotifications", false);

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
            if (strategy != null)
                strategy.run();
            sleep(50);
        }
    }

    @Override
    public void onPaint(Graphics g1) {

        if (paintInfo == null)
            return;

        if (showPaint) {

            Graphics2D g = (Graphics2D) g1;

            Image bg = paintInfo.getBackground();

            if (bg != null)
                g.drawImage(paintInfo.getBackground(), paintInfo.getBackgroundPosition().x, paintInfo.getBackgroundPosition().y, null);

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (PaintString paintString : paintInfo.getText(this.getRunningTime(), g) ) {
                PaintHelper.drawPaintString(paintString, g);
            }

            paintInfo.customDraw(g);
        } else {

            Image toggle = paintInfo.getButtonPaintToggle();

            if (toggle != null)
                g1.drawImage(toggle, paintInfo.getPaintToggleRectangle().x, paintInfo.getPaintToggleRectangle().y, null);
        }
    }

    @Override
    public OVERRIDE_RETURN overrideMouseEvent(MouseEvent e) {
        if (gui != null && e.getID() == MouseEvent.MOUSE_CLICKED) {

            if (paintInfo.getPaintToggleRectangle().contains(e.getPoint())) {

                this.showPaint = !this.showPaint;

                e.consume();
                return OVERRIDE_RETURN.DISMISS;
            } else if (paintInfo.getSettingsToggleRectangle().contains(e.getPoint())) {

                gui.show();

                e.consume();
                return OVERRIDE_RETURN.DISMISS;
            }
        }

        return OVERRIDE_RETURN.PROCESS;
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

    public OVERRIDE_RETURN overrideKeyEvent(KeyEvent e) {
        return OVERRIDE_RETURN.SEND;
    }

    @Override
    public void paintMouse(Graphics g, Point mousePos, Point dragPos) {
        PaintHelper.drawMouse(g, mousePos, dragPos);
    }

    @Override
    public void mouseClicked(Point p, int button, boolean isBot) {
        PaintHelper.mouseDown = true;
    }

    @Override
    public void paintMouseSpline(Graphics g, ArrayList<Point> points) {
        PaintHelper.drawMouseTrail(g, points);
    }

    // Unused overrides, feel free to override these in your script if you need them.
    public void mouseReleased(Point point, int button, boolean isBot) {}

    public void mouseMoved(Point point, boolean isBot) {
        PaintHelper.moveMouseTrail(point);
    }

    public void mouseDragged(Point point, int button, boolean isBot) {}
}
