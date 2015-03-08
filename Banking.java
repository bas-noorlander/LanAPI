package scripts.LanAPI;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSItem;

/**
 * @author Laniax
 *
 */
public class Banking {

	/** 
	 * Fetches the item(s) from the bank.
	 * 
	 * Uses webwalking to find the nearest bank.
	 * 
	 * @param id - the id(s) of the item(s) to fetch
	 * @param count -  the amount we should withdraw
	 */
	public static boolean getItemsFromBank(final int count, final String... names) {

		if (WebWalking.walkToBank()) {

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(50);
					return org.tribot.api2007.Banking.isInBank();
				}}, General.random(1000, 2000));

			if (org.tribot.api2007.Banking.openBank()) {

				// Pin is handled by Tribot.

				Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(50);
						return org.tribot.api2007.Banking.isBankScreenOpen();
					}}, General.random(1000, 2000)
						);


				for (final String name : names) {

					General.sleep(1000, 1500);
					RSItem[] withdrawItem = org.tribot.api2007.Banking.find(name);
					if (withdrawItem.length > 0 && withdrawItem[0].getStack() >= count) {

						org.tribot.api2007.Banking.withdrawItem(withdrawItem[0], count);

						Timing.waitCondition(new Condition() {
							public boolean active() {
								General.sleep(50);
								return Inventory.getCount(name) >= count;
							}}, General.random(1000, 2000));

					} else {
						// item not found in bank!
						return false;
					}
				}

				org.tribot.api2007.Banking.close();

				return true;
			}
		}
		return false;
	}

	/** 
	 * Brings your entire inventory to the bank.
	 * 
	 * Uses webwalking to find the nearest bank.
	 */
	public static boolean bringItemsToBank() {
		if (WebWalking.walkToBank()) {

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(50);
					return org.tribot.api2007.Banking.isInBank();
				}}, General.random(1000, 2000));

			if (org.tribot.api2007.Banking.openBank()) {

				// Pin is handled by Tribot.

				Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(50);
						return org.tribot.api2007.Banking.isBankScreenOpen();
					}}, General.random(1000, 2000)
						);

				org.tribot.api2007.Banking.depositAll();
				org.tribot.api2007.Banking.close(); // we don't return this because we can probably continue even if it fails.
				return true;
			}
		}
		return false;
	}

	/** 
	 * Brings your equipment to the bank.
	 * 
	 * Uses webwalking to find the nearest bank.
	 */
	public static boolean bringEquipmentToBank() {
		if (WebWalking.walkToBank()) {

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(50);
					return org.tribot.api2007.Banking.isInBank();
				}}, General.random(1000, 2000));

			if (org.tribot.api2007.Banking.openBank()) {

				// Pin is handled by Tribot.

				Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(50);
						return org.tribot.api2007.Banking.isBankScreenOpen();
					}}, General.random(1000, 2000)
						);

				if (org.tribot.api2007.Banking.depositEquipment())
					org.tribot.api2007.Banking.close(); // we don't return this because we can probably continue even if it fails.
				return true;
			}
		}
		return false;
	}

	/** 
	 * Brings the item(s) to the bank.
	 * 
	 * Uses webwalking to find the nearest bank.
	 * 
	 * @param id - the id(s) of the item(s) to bring
	 * @param count - the amount we should deposit
	 */
	public static boolean bringItemsToBank(final int count, final String... names) {

		if (WebWalking.walkToBank()) {

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(50);
					return org.tribot.api2007.Banking.isInBank();
				}}, General.random(1000, 2000));

			if (org.tribot.api2007.Banking.openBank()) {

				// Pin is handled by Tribot.

				Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(50);
						return org.tribot.api2007.Banking.isBankScreenOpen();
					}}, General.random(1000, 2000)
						);

				if (org.tribot.api2007.Banking.deposit(count, names)) {
					org.tribot.api2007.Banking.close(); // we don't return this because we can probably continue even if it fails.
					return true;
				}
			}
		}
		return false;
	}
}
