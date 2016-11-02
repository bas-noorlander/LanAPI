package scripts.lanapi.game.worldhopper;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSInterface;
import scripts.lanapi.game.concurrency.Condition;

import java.awt.*;
import java.util.HashMap;

/**
 * IngameWorldHopper
 *
 * Use as so: IngameWorldHopper.hop(worldNumber) (For example world 301 would be world 1, 381 would be 81 and so on)
 * Lack of null checking is due to the "Easier to Ask Forgiveness than Permission" (EAFP) programming methodology used here
 * Link to explanation: http://docstore.mik.ua/orelly/other/python/0596001886_pythonian-chp-6-sect-6.html
 * This link is specific to python, but also can apply to Java
 *
 * @author Final Calibur with inspiration from daxmagex, adjusments by laniax
 */
public class IngameWorldHopper {
    //constants

    //cache so we can just pull the appropriate child id for a world instead of looking for it every time
    private static final HashMap<Integer, Integer> WORLD_CHILDREN_CACHE = new HashMap<>();

    //normal logout inter constants
    private static final int NORMAL_LOGOUT_MASTER = 182;
    private static final int WORLD_SWITCHER_CHILD = 1;

    //world hopper inter constants
    private static final int HOPPER_MASTER = 69;
    private static final int HOPPER_CLOSE_CHILD = 3;
    private static final int LOADING_TEXT_CHILD = 2;
    private static final int HOPPER_LIST_CHILD = 7;
    private static final int MIN_Y = 228;
    private static final int MAX_Y = 418;

    //option dialogue constants
    private static final int DIALOGUE_MASTER = 219;
    private static final int DIALOGUE_CHILD = 0;
    private static final int DIALOGUE_OPTION_COMPONENT = 2;

    //misc. constants
    private static final long INTERFACE_SWITCH_TIMEOUT = 3500; //max wait time for world hopper inter to load
    private static final long INTERFACE_LOAD_TIMEOUT = 5000; //max wait time for world hopper inter to load

    //public API methods
    public static boolean hop(int world)
    {
        if(world > 100)
            world %= 100;

        //open hopper interface if necessary
        if(GameTab.open(GameTab.TABS.LOGOUT) && (isHopperUp() || openHopper()))
        {
            try
            {
                final RSInterface WORLD_LIST = Interfaces.get(HOPPER_MASTER, HOPPER_LIST_CHILD);
                final Integer WORLD_CHILD = WORLD_CHILDREN_CACHE.get(world);

                //load cache if necessary
                if((WORLD_CHILDREN_CACHE.isEmpty() || WORLD_CHILD == null) && WORLD_LIST != null)
                    loadCache(WORLD_LIST);

                final RSInterface TARGET_WORLD = WORLD_LIST.getChild(WORLD_CHILDREN_CACHE.get(world));

                //check if world is scrolled into view
                if(isWorldVisible(TARGET_WORLD) || scrollWorldIntoView(WORLD_LIST, TARGET_WORLD))
                    return clickWorld(WORLD_LIST.getChild(WORLD_CHILDREN_CACHE.get(world)), world) //Can't use cached TARGET_WORLD due to it's pos changing on scroll
                            && Timing.waitCondition(new Condition(() -> Login.getLoginState() == Login.STATE.INGAME && Game.getGameState() == 30), 6000);
            }
            catch(Exception e){}
        }


        return false;
    }

    //utility methods
    public static boolean isHopperUp()
    {
        return Interfaces.get(HOPPER_MASTER) != null;
    }

    private static boolean openHopper()
    {
        final RSInterface WORLD_SWITCHER_BUTTON = Interfaces.get(NORMAL_LOGOUT_MASTER, WORLD_SWITCHER_CHILD);

        if(WORLD_SWITCHER_BUTTON != null && Clicking.click(WORLD_SWITCHER_BUTTON))
            return Timing.waitCondition(new Condition(() -> Interfaces.get(HOPPER_MASTER) != null), INTERFACE_SWITCH_TIMEOUT) &&
                    Timing.waitCondition(new Condition(() -> {
                        RSInterface inter = Interfaces.get(HOPPER_MASTER, LOADING_TEXT_CHILD);
                        return inter != null && !inter.getText().equals("Loading...");
                    }), INTERFACE_LOAD_TIMEOUT);

        return false;
    }

    public static boolean closeHopper()
    {
        final RSInterface CLOSE_BUTTON = Interfaces.get(HOPPER_MASTER, HOPPER_CLOSE_CHILD);

        if(CLOSE_BUTTON != null && Clicking.click(CLOSE_BUTTON))
            return Timing.waitCondition(new Condition(() -> {
                RSInterface inter = Interfaces.get(HOPPER_MASTER);
                return inter == null || inter.isHidden();
            }), 1500);

        return false;
    }

    private static boolean isWorldVisible(RSInterface targetWorld)
    {
        Rectangle rect = targetWorld.getAbsoluteBounds();

        return rect.y > MIN_Y && rect.y < MAX_Y;
    }

    private static boolean scrollWorldIntoView(RSInterface worldList, RSInterface targetWorld)
    {
        final long START_TIME = Timing.currentTimeMillis();
        final long TIMEOUT = 7000;
        final Rectangle WORLD_LIST_BOUNDS = worldList.getAbsoluteBounds();

        Rectangle targetRectangle;

        do
        {
            //move mouse into world list interface if necessary
            if(!WORLD_LIST_BOUNDS.contains(Mouse.getPos()))
                Mouse.moveBox(WORLD_LIST_BOUNDS);

            //scroll in appropriate direction
            targetRectangle = targetWorld.getAbsoluteBounds();
            Mouse.scroll(targetRectangle.y < MIN_Y);
            if(Timing.timeFromMark(START_TIME) > TIMEOUT)
                return false;
            General.sleep(10, 40);
        }
        while(!isWorldVisible(targetWorld));

        General.sleep(70, 120);
        return true;
    }

    private static boolean clickWorld(RSInterface targetWorld, int worldNum)
    {
        if(Clicking.hover(targetWorld) && Timing.waitCondition(new Condition(()-> Game.isUptext(""+worldNum)), 1000))
        {
            Mouse.click(1);
            if(Timing.waitCondition(new Condition(() -> Interfaces.get(DIALOGUE_MASTER) != null), 1500))
            {
                final RSInterface DIALOGUE = Interfaces.get(DIALOGUE_MASTER, DIALOGUE_CHILD);
                Clicking.click(DIALOGUE.getChild(DIALOGUE_OPTION_COMPONENT));
            }

            return Timing.waitCondition(new Condition(() -> WorldHopper.getWorld() == worldNum), 5000);
        }

        return false;
    }

    private static void loadCache(RSInterface worldList)
    {
        final int FIRST_WORLD_COMPONENT = 0;
        final int WORLD_NUMBER_OFFSET = 2;
        final int WORLD_OFFSET = 6;

        for(int i = FIRST_WORLD_COMPONENT; i < worldList.getChildren().length; i += WORLD_OFFSET)
        {
            RSInterface mainChild = worldList.getChild(i);
            RSInterface worldNumChild = worldList.getChild(i + WORLD_NUMBER_OFFSET);

            if(mainChild != null && worldNumChild != null)
                WORLD_CHILDREN_CACHE.put(Integer.parseInt(worldNumChild.getText()), i);
        }
    }
}
