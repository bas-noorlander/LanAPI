package scripts.LanAPI.Core.GUI;

import javafx.fxml.Initializable;
import scripts.LanAPI.Core.Logging.LogProxy;

/**
 * @author Laniax
 */
public abstract class AbstractGUIController implements Initializable {

    LogProxy log = new LogProxy("AbstractGUIController");

    private GUI gui = null;

    public abstract boolean getEnableNotifications();

    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public GUI getGUI() {
        return this.gui;
    }
}
