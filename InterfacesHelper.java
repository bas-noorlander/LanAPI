package scripts.LanAPI;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceMaster;

import scripts.LanAPI.Constants.SpinningType;

/**
 * @author Laniax
 *
 */
public class InterfacesHelper { // Sadly, tribot's Interfaces class is declared final and cannot be extended.

	private final static int MASTER_MAKE_X = 548;
	private final static int CHILD_MAKE_X = 120;

	private final static int MASTER_SPINNING_WHEEL = 459;

	private final static int MASTER_COMBINATION_DOOR = 298;

	/**
	 * Checks if the 'Make X' chat window is open where a player may input an amount.
	 * @return true if open, false if not.
	 */
	public static boolean isMakeXOpen() {

		RSInterfaceChild i = org.tribot.api2007.Interfaces.get(MASTER_MAKE_X, CHILD_MAKE_X);
		if (i != null) {
			return !i.isHidden();
		}

		return false;
	}

	/**
	 * Provides logic to interact with a spinning wheel.
	 * Attempts to use the wheel if the interface is not open.
	 * 
	 * @param typeToString, the thing to string.
	 * @param amount, how many we should string
	 * @return true if succeeded, false if not.
	 */
	public static boolean handleSpinningWheel(SpinningType typeToString, int amount) {

		while (org.tribot.api2007.Interfaces.get(MASTER_SPINNING_WHEEL) == null) {
			Objects.interact("Spin");
		}

		General.sleep(1500, 3000);

		RSInterfaceChild child = org.tribot.api2007.Interfaces.get(MASTER_SPINNING_WHEEL, typeToString.getValue());
		if (child != null) {
			child.click("Make X");

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(50);
					return isMakeXOpen();
				}}, General.random(4000, 6000));

			General.random(1000, 3000);

			Keyboard.typeSend(Integer.toString(amount));

			General.random(1000, 2000);

			while (Player.getAnimation() != -1)
				General.sleep(500,1000);

			return true;
		}
		return false;
	}


	/**
	 * Solver for the Combination Lock Door as seen in the Fremennik Trials quest.
	 * 
	 * @param answer, the answer to the riddle
	 */
	public static boolean solveDoor(final String answer) {

		Timing.waitCondition(new Condition() {
			public boolean active() {
				return org.tribot.api2007.Interfaces.get(MASTER_COMBINATION_DOOR) != null;
			}}, General.random(1000, 2000));

		RSInterfaceMaster master = org.tribot.api2007.Interfaces.get(MASTER_COMBINATION_DOOR);
		if (master != null) {
			String[] letters = answer.split("(?<!^)");

			int Char = 43;
			int Next = 48;
			for (String letter : letters) {
				RSInterfaceChild i = master.getChild(Char);
				RSInterfaceChild btn = master.getChild(Next);
				while (!i.getText().equals(letter)) {
					btn.click("Ok");
					General.sleep(420,450);
				}
				Char++;
				Next+=2;
			}
			return master.getChild(57).click("Ok");
		}
		return false;
	}
}
