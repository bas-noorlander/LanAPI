package scripts.LanAPI.Core.GUI.Client.Frames;


import scripts.LanAPI.Core.GUI.Client.AbstractFrame;

import javax.swing.*;
import java.awt.*;

/**
 * @author Laniax
 */
public class TribotFrame extends AbstractFrame {

    public Frame find() {

        Frame[] frames = JFrame.getFrames();
        for (Frame frame : frames) {
            if (frame.getTitle().contains("TRiBot Old-School"))
                return frame;
        }

        return null;
    }
}