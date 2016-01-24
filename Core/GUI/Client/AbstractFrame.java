package scripts.LanAPI.Core.GUI.Client;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Laniax
 */
public abstract class AbstractFrame {

    private Frame frame = null;

    protected abstract Frame find();

    protected Frame get() {

        if (frame == null)
            frame = find();

        return frame;
    }

    /**
     * Gets all the components who are of the specified type.
     *
     * @param type - the type to search/return
     * @param <T>  - the type (eg. JPanel.class)
     * @return an ArrayList with the components, empty if none found.
     */
    public <T extends Component> ArrayList<Component> getComponentsByType(Class<T> type) {

        ArrayList<Component> result = new ArrayList<>();

        Frame frame;
        if ((frame = get()) != null) {

            for (Component component : getAllComponents(frame)) {
                if (component.getClass().getName().equals(type.getName())) {
                    result.add(component);
                }
            }
        }

        return result;
    }

    /**
     * Gets all the components on the frame.
     *
     * @return an arraylist with the components, empty if none found.
     */
    public ArrayList<Component> getAllComponents() {
        Frame gui;
        if ((gui = get()) != null)
            return getAllComponents(gui);

        return new ArrayList<Component>();
    }

    private ArrayList<Component> getAllComponents(final Container c) {

        ArrayList<Component> compList = new ArrayList<Component>();

        Component[] comps = c.getComponents();

        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container)
                compList.addAll(getAllComponents((Container) comp));
        }
        return compList;
    }

    /**
     * Brings the frame to the front.
     */
    public void toFront() {

        Frame gui;
        if ((gui = get()) != null) {
            gui.setState(Frame.NORMAL);
            gui.toFront();
        }
    }

    /**
     * Sets the window title.
     *
     * @param title
     */
    public void setTitle(String title) {

        Frame gui;
        if ((gui = get()) != null) {
            gui.setTitle(title);
        }
    }

}
