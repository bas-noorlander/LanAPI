package scripts.lanapi.game.camera;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Camera;
import scripts.lanapi.game.helpers.CameraHelper;

/**
 * @author Laniax
 */
public class LANCamera {

    private static LANCamera instance;

    private boolean use_async = false;

    private CameraMode mode = CameraMode.ABC2;

    public static LANCamera get() {

        if (instance == null) {
            instance = new LANCamera();
        }

        return instance;
    }

    public void turnToTile(Positionable position) {

        if (use_async) {
            CameraHelper.get().turnToTile(position);
        } else {
            Camera.turnToTile(position);
        }
    }

    /**
     * Sets the camera mode the script should use.
     * @param mode
     */
    public void setMode(CameraMode mode) {

        if (mode == null)
            return;

        if (mode != CameraMode.ASYNC_CAMERA)
            use_async = false;

        this.mode = mode;
    }

    public void initialize() {

        switch (mode) {
            case ABC2:
                Camera.setRotationMethod(Camera.ROTATION_METHOD.DEFAULT);
                break;
            case MIDDLE_MOUSE:
                Camera.setRotationMethod(Camera.ROTATION_METHOD.ONLY_MOUSE);
                break;
            case ARROW_KEYS:
                Camera.setRotationMethod(Camera.ROTATION_METHOD.ONLY_KEYS);
                break;
        }
    }
}
