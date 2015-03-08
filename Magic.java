package scripts.LanAPI;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSArea;

import scripts.LanAPI.Constants.Locations;

/**
 * @author Laniax
 *
 */
public class Magic {

	/**
	 *  Casts the lumbridge home teleport and checks if it succeeded.
	 * 
	 * @return true if in lumbridge, false if otherwise.
	 */
	public static boolean castLumbridgeHomeTeleport() {

		GameTab.open(TABS.MAGIC);
		if (org.tribot.api2007.Magic.selectSpell("Lumbridge Home Teleport")) {
			General.sleep(1500,2000);

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(50);
					return Player.getAnimation() == -1;
				}
			}, General.random(15000, 20000));
		}
		if ((new RSArea(Locations.POS_LUMBRIDGE_CENTER, 30).contains(Player.getRSPlayer()))) 
			return true;

		return false;
	}
}
