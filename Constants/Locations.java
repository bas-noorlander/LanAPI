package scripts.LanAPI.Constants;

import org.tribot.api.General;
import org.tribot.api2007.types.RSTile;

/**
 * @author Laniax
 *
 */
public final class Locations {
	
	public final static RSTile POS_LUMBRIDGE_CENTER = new RSTile(3217, 3219, 0).translate(General.random(-3, 3), General.random(-3, 3)); // anti-LCP detection

}
