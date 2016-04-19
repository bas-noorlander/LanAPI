package scripts.lanapi.game.friends;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import scripts.lanapi.game.concurrency.Condition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alphadog, adjustments by Laniax
 */
public class Friends {

    private final static int PARENT_WIDGET = 429;
    private final static int FRIEND_CONTAINER = 3;
    private final static int ADD_FRIEND = 5;
    private final static int DELETE_FRIEND = 6;

    /**
     * Gets all the {@link RSFriend}'s that are in your friend list.
     *
     * @return Array with all your {@link RSFriend}'s
     */
    public static List<RSFriend> getAll() {
        final RSInterfaceChild container = Interfaces.get(PARENT_WIDGET, FRIEND_CONTAINER);
        final List<RSFriend> friends = new ArrayList<>();

        if (container != null) {
            if (container.getChildren() == null) { //Friends are not loaded yet
                GameTab.open(GameTab.TABS.FRIENDS);
                Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        General.sleep(50, 100);
                        return GameTab.getOpen() == GameTab.TABS.FRIENDS;
                    }
                }, General.random(400,800));
            }

            RSInterfaceComponent[] children = container.getChildren();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    RSInterfaceComponent nameInterface = children[i];
                    RSInterfaceComponent statusInterface = children[children[++i].getTextureID()==-1?i:++i];
                    friends.add(new RSFriend(nameInterface, statusInterface));
                }
            }
        }

        return friends;
    }

    /**
     * Gets all the {@link RSFriend}'s that are in your friend list.
     *
     * @param online <tt>true</tt> if you want to get all your online friends,
     *               <tt>false</tt> if you want to get all your offline friends
     * @return Array with all your {@link RSFriend}'s
     */
    public static RSFriend[] getAll(boolean online) {
        final ArrayList<RSFriend> friends = new ArrayList<>();
        for (RSFriend friend : getAll()) {
            if (friend.isOnline() == online) {
                friends.add(friend);
            }
        }

        return friends.toArray(new RSFriend[friends.size()]);
    }


    /**
     * Gets the {@link RSFriend} that matches the specified name
     * Note: Will return <tt>null</tt> if no {@link RSFriend} is found.
     * Note: The doesn't have to be case sensitive
     *
     * @return {@link RSFriend} that matches the specified name
     */
    public static RSFriend getFriend(String name) {
        for (RSFriend friend : getAll()) {
            if (friend.getName().equalsIgnoreCase(name))
                return friend;
        }

        return null;
    }

    /**
     * Gets the amount of friends in your friend list
     *
     * @return amount of friends in your friend list
     */
    public static int getAmount() {
        return getAll().size();
    }

    /**
     * Adds a friend to your friend list
     *
     * @param name the name of the friend you want to add
     * @return <tt>true</tt> if the friend is successfully added
     */
    public static boolean addFriend(String name) {
        if (Friends.getFriend(name) != null) return true;
        final RSInterfaceChild addButton = Interfaces.get(PARENT_WIDGET, ADD_FRIEND);
        final int count = getAmount();
        return clickFriendButton(addButton, name) && Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                General.sleep(50, 100);
                return getAmount() < count;
            }
        }, 3000);
    }

    /**
     * Deletes a friend to your friend list
     *
     * @param name the name of the friend you want to delete
     * @return <tt>true</tt> if the friend is successfully deleted
     */
    public static boolean deleteFriend(String name) {
        if (Friends.getFriend(name) == null) return true;
        final RSInterfaceChild deleteButton = Interfaces.get(PARENT_WIDGET, DELETE_FRIEND);
        final int count = getAmount();
        return clickFriendButton(deleteButton, name) && Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                General.sleep(50, 100);
                return getAmount() < count;
            }
        }, 3000);
    }

    /**
     * Clicks the specified button and enters the friend name afterwards
     *
     * @param button     the interface child to click
     * @param friendName the friend name to type afterwards
     * @return <tt>true</tt> if the button is successfully clicked
     */
    private static boolean clickFriendButton(RSInterfaceChild button, String friendName) {
        if (button != null && GameTab.open(GameTab.TABS.FRIENDS) && Clicking.click(button)) {
            General.sleep(1100, 1200); //todo: this is a no-go
            Keyboard.typeString(friendName);
            General.sleep(300, 500);
            Keyboard.pressEnter();
            return true;
        }
        return false;
    }
}