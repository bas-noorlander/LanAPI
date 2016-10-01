package scripts.lanapi.core.gui;

import javafx.fxml.Initializable;
import scripts.lanapi.core.logging.LogProxy;

/**
 * @author Laniax
 */

public abstract class AbstractGUIController implements Initializable {

    protected LogProxy log = new LogProxy("AbstractGUIController");

    private GUI gui = null;

    public abstract boolean getEnableNotifications();

    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public GUI getGUI() {
        return this.gui;
    }
}
