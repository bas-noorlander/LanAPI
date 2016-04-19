package scripts.lanapi.game.helpers;

import org.tribot.api.Clicking;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Clickable;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSMenuNode;
import scripts.lanapi.core.logging.LogProxy;
import scripts.lanapi.game.combat.Hovering;

import java.awt.*;

/**
 * @author Laniax
 */
public class ChooseOptionHelper { // Sadly, tribot's ChooseOption class is declared final and cannot be extended.

    private static LogProxy log = new LogProxy("ChooseOptionHelper");

    public static boolean hover(String option, Clickable entity) {

        if (!ChooseOption.isOpen() || !ChooseOption.isOptionValid(option))
            return false;

        RSMenuNode[] nodes = ChooseOption.getMenuNodes();

        for (RSMenuNode node : nodes) {

            if (!node.correlatesTo(entity))
                return false;

            if (!node.getAction().contains(option))
                continue;

            return hoverMenuNode(node);

        }

        return false;
    }

    public static boolean isMenuOpenForEntity(Clickable entity) {

        if (!ChooseOption.isOpen())
            return false;

        RSMenuNode[] nodes = ChooseOption.getMenuNodes();

        return nodes.length > 0 && nodes[0] != null && nodes[0].correlatesTo(entity);
    }

    /**
     * Hovers an RSMenuNode
     * @param node
     * @return
     */
    public static boolean hoverMenuNode(RSMenuNode node) {

        Rectangle rect = node.getArea();

        if (rect != null) {

            if (!rect.contains(Mouse.getPos())) {

                RSItem tmp = new RSItem(0, 0, 0, RSItem.TYPE.OTHER);
                tmp.setArea(rect);

                Hovering.setHoveringItem(tmp);

                return Clicking.hover(tmp);

            } else
                return true;
        }


        return false;
    }

}
