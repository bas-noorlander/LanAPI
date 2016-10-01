package scripts.lanapi.core.gui.client;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tribot.api.General;
import org.tribot.api.util.Screenshots;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;
import scripts.lanapi.core.gui.GUI;
import scripts.lanapi.core.gui.client.frames.BugReportController;
import scripts.lanapi.core.gui.client.frames.TribotFrame;
import scripts.lanapi.core.io.json.JsonObject;
import scripts.lanapi.game.painting.PaintHelper;
import scripts.lanapi.game.persistance.Vars;
import scripts.lanapi.game.script.LANScript;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public static GUI createBugReportGUI() {

        LANScript script = Vars.get().get("script");

        final RSTile player_pos = Player.getPosition();
        final BufferedImage screenshot = Screenshots.getScreenshotImage();

        final JsonObject obj = new JsonObject();

        for (Map.Entry<String, Object> set : Vars.get().getAll()) {
            obj.add(set.getKey(), set.getValue().toString());
        }

        StringBuilder sb = new StringBuilder();

        for (StackTraceElement ste : script.thread.getStackTrace()) {
            sb.append(ste.toString()+"\\r\\n");
        }

        obj.add("stacktrace", sb.toString());
        obj.add("runtime", script.getRunningTime());

        Platform.runLater(() -> {

            try {

                FXMLLoader loader = new FXMLLoader(new URL("http://laniax.eu/paint/bugreport.fxml"));
                // By default FXMLLoader uses a different classloader, this caused issues with upcasting
                loader.setClassLoader(script.getClass().getClassLoader());

                Parent root1 = loader.load();
                Stage stage = new Stage();
                stage.setResizable(false);
                stage.setTitle(script.getScriptName()+" - Report a bug");
                stage.setScene(new Scene(root1));

                BugReportController controller = loader.getController();
                controller.setStage(stage);
                controller.player_pos = player_pos;
                controller.screenshot = screenshot;
                controller.data = obj;

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return null;
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
