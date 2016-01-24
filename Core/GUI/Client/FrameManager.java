package scripts.LanAPI.Core.GUI.Client;

import scripts.LanAPI.Core.GUI.Client.Frames.TribotFrame;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Takes care of all the frames the client or script produces
 * <p>
 * Do not use this directly.
 *
 * @author Laniax
 */
public class FrameManager {

    private static final Object _lock = new Object();

    public static List<AbstractFrame> _frames = new ArrayList<>(Arrays.asList(
            new TribotFrame()
    ));

    public static void addFrame(AbstractFrame frame) {
        _frames.add(frame);
    }

    /**
     * Brings all the registered frames to the front.
     */
    public static void toFront() {

        synchronized (_lock) {
            for (AbstractFrame frame : _frames) {

                Frame gui;
                if ((gui = frame.get()) != null) {
                    gui.setState(Frame.NORMAL);
                    gui.toFront();
                }
            }
        }
    }

    /**
     * Sets the window title of all registered frames.
     *
     * @param title
     */
    public static void setTitle(String title) {

        synchronized (_lock) {
            for (AbstractFrame frame : _frames) {

                Frame gui;
                if ((gui = frame.get()) != null) {
                    gui.setTitle(title);
                }
            }
        }
    }
}
