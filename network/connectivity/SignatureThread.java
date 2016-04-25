package scripts.lanapi.network.connectivity;

import org.tribot.api.General;
import org.tribot.api2007.Login;
import org.tribot.api2007.types.RSItem;
import scripts.lanapi.game.concurrency.Condition;
import scripts.lanapi.game.concurrency.observers.inventory.InventoryListener;
import scripts.lanapi.game.inventory.Inventory;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

/**
 * @author Laniax
 */
public class SignatureThread extends Thread {

    private DynamicSignatures data;

    private final int timeout;

    private boolean running = true;

    public SignatureThread() {
        timeout = Signature.get().getTimeout();
    }

    @Override
    public void run() {

        while (this.running) {

            if (!Signature.get().hasSession()) {
               if (!Signature.get().startSession()) {
                   General.println("Error starting a signature session. Data will NOT be collected.");
                   return;
               }
            }

            if (Signature.get().send(this.data)) {
                General.sleep(TimeUnit.MINUTES.toMillis(this.timeout));
            } else {
                General.println("Error sending signature data. We will retry in 1 minute.");
                General.sleep(TimeUnit.MINUTES.toMillis(1));
            }
        }
    }

    public void end() {
        running = false;
    }

    public void setData(DynamicSignatures data) {
        this.data = data;
    }

}
