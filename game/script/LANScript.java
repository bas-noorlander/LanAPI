package scripts.lanapi.game.script;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
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
import scripts.lanapi.core.patterns.IStrategy;
import scripts.lanapi.game.concurrency.Condition;
import scripts.lanapi.game.concurrency.observers.inventory.InventoryListener;
import scripts.lanapi.core.patterns.StrategyList;
import scripts.lanapi.game.concurrency.observers.inventory.InventoryObserver;
import scripts.lanapi.game.helpers.SkillsHelper;
import scripts.lanapi.game.painting.AbstractPaintInfo;
import scripts.lanapi.game.painting.PaintHelper;
import scripts.lanapi.game.persistance.Vars;
import scripts.lanapi.network.connectivity.DynamicSignatures;
import scripts.lanapi.network.connectivity.Signature;
import scripts.lanapi.network.connectivity.SignatureThread;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LANScript extends Script implements Painting, MouseActions, MousePainting, MouseSplinePainting,
        EventBlockingOverride, Ending, Breaking, MessageListening07, InventoryListener, DynamicSignatures {

    protected boolean has_arguments = false;
    protected LogProxy log;
    protected boolean wait_for_gui = true;
    protected boolean show_paint = false;
    protected GUI gui = null;
    protected BufferedImage icon = null;
    public AbstractPaintInfo paint_info = null;
    protected Color mouse_color = Color.BLACK;
    protected InventoryObserver observer = null;

    public Thread thread;

    private boolean quitting = false;
    private boolean inventory_observer_running = false;
    private final Pattern skillup_regex = Pattern.compile("(?<=\\ba\\s)\\w+"); // Pattern.compile("(?<=\\ba\\s)(\\w+).*\\b([0-9]{1,2})");
    private SignatureThread signatures;

    /**
     * Returns if the script is quitting. IE, finishing the last loop and disposing of any objects.
     * @return
     */
    public boolean isQuitting() {
        return this.quitting;
    }

    /**
     * Sets if the script is quitting. IE, finishing the last loop and disposing of any objects.
     */
    public void setQuitting(boolean value) {
        this.quitting = value;
    }

    /**
     * Returns if this script is being run locally or on the repository.
     * @return
     */
    public boolean isLocal() {
        return this.getRepoID() == -1;
    }

    /**
     * This method is called once when the script starts and we are logged ingame, just before the paint/gui shows.
     */
    public void onScriptStart() {}

    /**
     * This method is called when the GUI closes when starting the script.
     * This is never called if you do not have a GUI.
     */
    public void onGUIClosed() {}

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


    /**
     * Checks if an inventory observer is running.
     * @return
     */
    public boolean isInventoryObserverRunning() {
        return this.inventory_observer_running;
    }

    /**
     * Sets if we want to use a inventory observer or not. This will enable the #inventoryItemAdded and #inventoryItemRemoved functions to get called.
     * @param state
     */
    public void setInventoryObserverState(boolean state) {

        if (state) {

            if (this.observer == null) {
                this.observer = new InventoryObserver(new Condition(() -> !Banking.isBankScreenOpen()));
                this.observer.addListener(this);
            }

            this.observer.start();
            this.inventory_observer_running = true;

        } else if (this.observer != null) {
            this.observer.end();

        this.inventory_observer_running = false;
    }
}

    /**
     * This is the entry point of a tribot script. You shouldn't have to change anything here.
     */
    @Override
    public void run() {
        Vars.get().add("script", this);
        this.thread = Thread.currentThread();

        this.paint_info = this.getPaintInfo();
        this.log = new LogProxy(this);
        this.icon = this.getNotificationIcon();
        this.gui = this.getGUI();

        if (this.paint_info != null)
            this.mouse_color = paint_info.getPrimaryColor();

        String signature_url = this.signatureServerUrl();
        if (signature_url != null) {
            Signature.get().setUrl(signature_url);
            this.signatures = new SignatureThread();
            this.signatures.setData(this);
            this.signatures.start();
        }

        log.debug("Hey, thanks for trying LanAPI! These debug messages won't show if you upload this script to the repository.");
        log.debug("You can use log.debug() to write messages like this. Or log.info() to write a normal message!");

        General.useAntiBanCompliance(true);

        ThreadSettings.get().setClickingAPIUseDynamic(true);

        while (Login.getLoginState() != Login.STATE.INGAME) {
            sleep(250);
            Login.login();
        }

        SkillsHelper.setStartSkills();
        this.show_paint = true;

        this.onScriptStart();

        if (!this.has_arguments && this.gui != null) {

            this.gui.show();

            PaintHelper.status_text = "GUI is open";
            while (this.gui.isOpen())
                sleep(250);

            if (this.isQuitting()) // a gui is able to quit the script.
                return;

            this.onGUIClosed();
        }

        PaintHelper.status_text = "Preparing script..";

        boolean use_notifications = Vars.get().get("enableNotifications", false);

        if (icon != null && use_notifications) {
            if (Notifications.init(this))
                MessageListener.addListener(this);
        }

        StrategyList list = new StrategyList(getStrategies());

        Antiban.setWaitingSince();

        while (!this.isQuitting()) {

            IStrategy strategy = list.getValid();
            if (strategy != null) {
                strategy.run();
            }

            sleep(50);
        }
    }

    /**
     * This is the entry point for a script's paint. You shouldn't have to do anything here, please provide a PaintInfo object in #getPaintInfo instead.
     * @param g1
     */
    @Override
    public void onPaint(Graphics g1) {

        if (this.paint_info == null)
            return;

        Graphics2D g = (Graphics2D) g1;

        if (this.show_paint) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            this.paint_info.draw(g);
        }
    }

    /**
     * This method is called once when the script stops completely. (ie. user clicked the stop button or the script queue ran out.)
     * Please call super() when overriding this method.
     */
    @Override
    public void onEnd() {

        log.info("Thank you for using %s! You ran this script for %s. If you enjoyed this script, please leave a message on the forums :)", this.getScriptName(), Timing.msToString(this.getRunningTime()));

        Notifications.destroy();

        if (this.signatures != null && Signature.get().hasSession()) {
            Signature.get().send(this);
            this.signatures.end();
        }

        this.setInventoryObserverState(false);

        if (this.gui != null)
            this.gui.close();
    }

    /**
     * This method is called once when a break starts that was scheduled by the user in the Break Manager.
     * Please call super() when overriding this method.
     */
    @Override
    public void onBreakStart(long breakTime) {

        if (Notifications.getPreferences().isOnBreakStart())
            Notifications.send(this.getScriptName(), "Taking a break for " + Timing.msToString(breakTime), TrayIcon.MessageType.INFO);
    }

    /**
     * This method is called once when a break ends that was scheduled by the user in the Break Manager.
     * Please call super() when overriding this method.
     */
    @Override
    public void onBreakEnd() {

        if (Notifications.getPreferences().isOnBreakEnd())
            Notifications.send(this.getScriptName(), "Break Ended", TrayIcon.MessageType.INFO);
    }

    /**
     * This method is called when the server sends a message to the player.
     * Please call super() when overriding this method.
     * @param s the string the server sent
     */
    @Override
    public void serverMessageReceived(String s) {

        if (Notifications.getPreferences().isOnSkillLevelUp()) {

            if (s.contains("Congratulations, you just advanced")) {

                Matcher match = this.skillup_regex.matcher(s);

                if (match.find()) {
                    String skill_name = match.group(0);
                    Skills.SKILLS skill = Skills.SKILLS.valueOf(skill_name.toUpperCase());
                    if (skill != null) {
                        int new_level = skill.getActualLevel();
                        Notifications.send(String.format("%s level up!", skill_name), String.format("Your %s advanced to level %d!", skill_name, new_level), TrayIcon.MessageType.INFO);
                        log.info("You gained a level in %s! You are now %d.", skill_name, new_level);
                    }
                }
            }
        }

        if (Notifications.getPreferences().isOnServerMessage())
            Notifications.send("[Server Message]", s, TrayIcon.MessageType.WARNING);

    }

    /**
     * This method is called when the player receives a message in the clan chat.
     * Please call super() when overriding this method.
     * @param name the name of the sender
     * @param msg the message sent
     */
    @Override
    public void clanMessageReceived(String name, String msg) {

        if (Notifications.getPreferences().isOnClanMessage())
            Notifications.send(String.format("[Clan] %s:", name), msg);
    }

    /**
     * This method is called when a nearby player sends a message in the regular chat.
     * Please call super() when overriding this method.
     * @param name the name of the sender
     * @param msg the message sent
     */
    @Override
    public void playerMessageReceived(String name, String msg) {

        if (Notifications.getPreferences().isOnChatMessage())
            Notifications.send(String.format("[Chat] %s:", name), msg);

    }

    /**
     * This method is called when the player receives a private message.
     * Please call super() when overriding this method.
     * @param name the name of the sender
     * @param msg the message sent
     */
    @Override
    public void personalMessageReceived(String name, String msg) {

        if (Notifications.getPreferences().isOnPrivateMessage())
            Notifications.send(String.format("[PM] %s:", name), msg, TrayIcon.MessageType.WARNING);

    }

    /**
     * This method is called when the player receives a duel request.
     * Please call super() when overriding this method.
     * @param name the name of the sender
     * @param msg the message sent
     */
    @Override
    public void duelRequestReceived(String name, String msg) {

        if (Notifications.getPreferences().isOnDuelRequest())
            Notifications.send("[Duel Request]", String.format("From '%s'", name), TrayIcon.MessageType.WARNING);
    }

    /**
     * This method is called when the player receives a trade request.
     * Please call super() when overriding this method.
     * @param s the name of the sender
     */
    @Override
    public void tradeRequestReceived(String s) {

        if (Notifications.getPreferences().isOnTradeRequest())
            Notifications.send("[Trade Request]", String.format("From '%s'", s), TrayIcon.MessageType.WARNING);

    }

    /**
     * This is called by tribot to paint our mouse. You shouldn't have to do anything here.
     * @param g
     * @param mouse_pos
     * @param drag_pos
     */
    @Override
    public void paintMouse(Graphics g, Point mouse_pos, Point drag_pos) {
        PaintHelper.drawMouse(g, mouse_pos, drag_pos, this.mouse_color);
    }

    /**
     * This is called by tribot when the user or script clicks the mouse. You shouldn't have to do anything here.
     * @param p
     * @param button
     * @param is_bot
     */
    @Override
    public void mouseClicked(Point p, int button, boolean is_bot) {
        PaintHelper.mouse_down = true;
    }

    /**
     * This is called by tribot to paint the mouse trail. You shouldn't have to do anything here.
     * @param g
     * @param points
     */
    @Override
    public void paintMouseSpline(Graphics g, ArrayList<Point> points) {
        PaintHelper.drawMouseTrail(g, points, this.mouse_color);
    }

    /**
     * This is called by tribot when the user or script moves the mouse. You shouldn't have to do anything here.
     * @param point
     * @param is_bot
     */
    @Override
    public void mouseMoved(Point point, boolean is_bot) {
        PaintHelper.moveMouseTrail(point);
    }

    public OVERRIDE_RETURN overrideMouseEvent(MouseEvent e) {
        if (e.getID() == MouseEvent.MOUSE_CLICKED) {

            for (Map.Entry<Integer, Shape> set : this.paint_info.getAllClickableShapes()) {
                if (set.getValue().contains(e.getPoint())) {
                    this.paint_info.onShapeClicked(set.getKey());
                    e.consume();

                    return OVERRIDE_RETURN.DISMISS;
                }
            }
        }

        return OVERRIDE_RETURN.PROCESS;
    }

    // Stubs
    public void mouseReleased(Point point, int button, boolean is_bot) {}
    public void mouseDragged(Point point, int button, boolean is_bot) {}
    public OVERRIDE_RETURN overrideKeyEvent(KeyEvent e) {return OVERRIDE_RETURN.SEND;}
    public void inventoryItemRemoved(RSItem item, int count) {}
    public void inventoryItemAdded(RSItem item, int count) {}
}
