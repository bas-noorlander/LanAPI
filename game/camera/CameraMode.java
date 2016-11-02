package scripts.lanapi.game.camera;

import org.tribot.api2007.Camera;

/**
 * @author Laniax
 */
public enum CameraMode {

    ABC2("Default (ABC2)"),
    ASYNC_CAMERA("Asynchronous Camera"),
    ARROW_KEYS("Arrow keys only"),
    MIDDLE_MOUSE("Middle mouse button only");

    private String gui_name;

    CameraMode(String gui_name) {
        this.gui_name = gui_name;
    }

    @Override
    public String toString() {
        return this.gui_name;
    }
}
