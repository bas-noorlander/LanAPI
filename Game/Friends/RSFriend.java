package scripts.lanapi.game.friends;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSInterfaceComponent;
import scripts.lanapi.game.concurrency.Condition;

/**
 * @author Alphadog, adjusments by Laniax
 */
final public class RSFriend {

    private final RSInterfaceComponent statusComponent;
    private final RSInterfaceComponent nameComponent;
    private final String name;

    RSFriend(RSInterfaceComponent nameComponent, RSInterfaceComponent statusComponent) {
        this.name = nameComponent.getText().replaceAll(""+(char)160, ""+(char)32);
        this.nameComponent = nameComponent;
        this.statusComponent = statusComponent;
    }

    public String getName() {
        return name;
    }

    public boolean isOnline() {
        return !statusComponent.getText().equals("Offline");
    }

    public boolean delete() {
        final int count = Friends.getAmount();
        return GameTab.open(GameTab.TABS.FRIENDS) && nameComponent.click("Delete") && Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                General.sleep(50, 100);
                return Friends.getAmount() < count;
            }
        }, 1200);
    }

    public int getWorld() {
        return isOnline() ? Integer.valueOf(statusComponent.getText().substring(statusComponent.getText().length() - 3)) : -1;
    }

    public boolean hopTo() {
        final int world = getWorld();
        return world != -1 && WorldHopper.changeWorld(world);
    }

    @Override
    public String toString() {
        return name + " - " + (isOnline() ? "Online - World " + getWorld() : "Offline");
    }
}