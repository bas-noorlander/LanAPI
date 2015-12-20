package scripts.LanAPI.Game.Script;

import org.tribot.api.General;
import org.tribot.api.ListenerManager;
import org.tribot.api.Timing;
import org.tribot.api2007.Login;
import org.tribot.api2007.util.ThreadSettings;
import org.tribot.script.Script;
import org.tribot.script.interfaces.*;
import scripts.LanAPI.Core.Logging.LogProxy;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.Concurrency.StrategyList;
import scripts.LanAPI.Game.Painting.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public abstract class AbstractScript extends Script implements Painting, MouseActions, MousePainting, MouseSplinePainting, EventBlockingOverride {

    protected LogProxy log;

    private boolean quitting = false;
    private boolean waitForGUI = true;
    protected boolean showPaint = false;

    public abstract IStrategy[] getStrategies();

    private JFrame gui = null;
    private AbstractPaintInfo paintInfo = null;
    public abstract JFrame getGUI();

    /**
     * This method is called once when the script starts and we are logged ingame, just before the paint/gui shows.
     */
    public abstract void onInitialize();

    public abstract AbstractPaintInfo getPaintInfo();

    @Override
    public final void run() {

        log = new LogProxy(this);

        log.debug("Hey, thanks for trying LanAPI! These debug messages won't show if you upload this script to the repository.");
        log.debug("You can use log.debug() to write messages like this. Or log.info() to write a normal message!");

        ListenerManager.add(this);

        General.useAntiBanCompliance(true);

        ThreadSettings.get().setClickingAPIUseDynamic(true);

        // wait until login bot is done.
        while (Login.getLoginState() != Login.STATE.INGAME)
            sleep(250);

        onInitialize();

        gui = getGUI();
        paintInfo = getPaintInfo();

        showPaint = true;

        if (gui != null) {

            gui.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentHidden(ComponentEvent e) {
                    super.componentHidden(e);
                    waitForGUI = false;
                }
            });

            EventQueue.invokeLater(() -> gui.setVisible(true));
        }

        while (waitForGUI)
            sleep(250);

        StrategyList list = new StrategyList(getStrategies());

        while (!quitting) {
            IStrategy strategy = list.getValid();
            if (strategy != null)
                strategy.run();
            sleep(50);
        }

        log.info("Thank you for using %s! You ran this script for %s. If you enjoyed this script, please leave a message on the forums :)", this.getScriptName(), Timing.msToString(this.getRunningTime()));

    }

    @Override
    public void onPaint(Graphics g1) {

        if (paintInfo == null)
            return;

        if (showPaint) {

            Graphics2D g = (Graphics2D)g1;

            g.drawImage(paintInfo.getBackground(), paintInfo.getBackgroundPosition().x, paintInfo.getBackgroundPosition().y, null);

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (PaintString paintString : paintInfo.getText(this.getRunningTime())) {

                if (paintString.isShadowed()) {
                    PaintHelper.drawShadowedText(paintString, g);
                } else {
                    g.setColor(paintString.getColor());
                    g.drawString(paintString.getString(), paintString.getPosition().x, paintString.getPosition().y);
                }

            }

        } else {
            g1.drawImage(paintInfo.getButtonPaintToggle(), paintInfo.getPaintToggleRectangle().x, paintInfo.getPaintToggleRectangle().y, null);
        }
    }

    @Override
    public OVERRIDE_RETURN overrideMouseEvent(MouseEvent e) {
        if ( gui != null && e.getID() == MouseEvent.MOUSE_CLICKED ) {

            if (paintInfo.getPaintToggleRectangle().contains(e.getPoint())) {

                this.showPaint = !this.showPaint;

                e.consume();
                return OVERRIDE_RETURN.DISMISS;
            } else if (paintInfo.getSettingsToggleRectangle().contains(e.getPoint())) {

                gui.setVisible(gui.isVisible());

                e.consume();
                return OVERRIDE_RETURN.DISMISS;
            }
        }

        return OVERRIDE_RETURN.PROCESS;
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
