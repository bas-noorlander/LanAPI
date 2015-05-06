package scripts.LanAPI;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSInterfaceMaster;
import org.tribot.script.interfaces.EventBlockingOverride;

enum QuestStatus {
	COMPLETED,
	IN_PROGRESS,
	NOT_STARTED,
	INVALID
};

/**
 * Helper class with functions that are useful during questing.
 * 
 * @author Laniax
 *
 */
public class Quests implements EventBlockingOverride {

	private static final int QUEST_INTERFACE_MASTER = 274;
	private static final int QUEST_INTERFACE_SCROLLBAR = 13;
	private static final int CHILD_QUEST_POINTS = 10;
	private static final int QUEST_COMPLETE_MASTER = 277;

	private static final int QUEST_JOURNAL_MASTER = 275;
	private static final int CHILD_JOURNAL_CLOSE = 134;

	private static final double SCROLL_Y_PER_PIXEL = 10.15463917525773;

	private static final int QUEST_COLOR_GREEN = 65280;
	private static final int QUEST_COLOR_RED = 16711680;
	private static final int QUEST_COLOR_YELLOW = 16776960;

	private static RSInterfaceMaster questMaster = null;
	private static RSInterfaceChild questPointChild = null;

	private static RSInterfaceMaster questJournalMaster = null;

	private static boolean blockMouse = false;


	/**
	 * Gets the progress status of the quest based on the color of the text in the quest list.
	 * 
	 * This only opens the quest tab if necessary and doesn't use the mouse in any other way.
	 * 
	 * @param Quest to get status for
	 * @return The QuestStatus or null if something went wrong.
	 * 
	 * @deprecated use appropiate quest setting instead!
	 */
	public static QuestStatus getQuestStatus(scripts.LanAPI.Constants.Quests quest) {
		blockMouse = true;
		//TABS oldtab = GameTab.getOpen();
		GameTab.open(TABS.QUESTS);
		General.sleep(100,150);
		if (GameTab.getOpen() == TABS.QUESTS) {
			scrollToQuest(quest);
			questMaster = Interfaces.get(QUEST_INTERFACE_MASTER);
			RSInterfaceChild questChild = questMaster.getChild(quest.getValue());
			if (questChild != null) {
				int color = questChild.getTextColour();
				blockMouse = false;
				//GameTab.open(oldtab);
				switch (color) {
				case QUEST_COLOR_GREEN:
					return QuestStatus.COMPLETED;
				case QUEST_COLOR_RED:
					return QuestStatus.NOT_STARTED;
				case QUEST_COLOR_YELLOW:
					return QuestStatus.IN_PROGRESS;
				default:
					General.println("Unknown quest color:"+color+". Report on the forums!");
					return null;
				}
			}
		}
		blockMouse = false;
		return QuestStatus.INVALID;
	}

	/**
	 * Gets if the quest completion screen is open
	 * 
	 * @return true if open, false if closed.
	 */
	public static boolean isQuestCompleteScreenOpen() {
		return Interfaces.get(QUEST_COMPLETE_MASTER) != null;
	}

	/**
	 * Opens the quest tab, scrolls to the quest, clicks it and returns the last string that isn't crossed out.
	 * 
	 * The string is stripped of any HTML.
	 * 
	 * @param Quest to click on
	 * @return The last string in the quest's journal or an empty string if something went wrong.
	 */
	public static String getQuestProgress(scripts.LanAPI.Constants.Quests quest) {
		blockMouse = true;
		if (scrollToQuest(quest)) {
			General.sleep(250,500);
			questMaster = Interfaces.get(QUEST_INTERFACE_MASTER);
			if (questMaster != null) {
				RSInterfaceChild questChild = questMaster.getChild(quest.getValue());
				if (questChild != null) {
					Rectangle rect =  questChild.getAbsoluteBounds();
					Mouse.clickBox((int)rect.getMinX()+3, (int)rect.getMinY()+3, (int)rect.getMaxX()-3, (int)rect.getMaxY()-3, 1);
					General.sleep(1000,2000);
					questJournalMaster = Interfaces.get(QUEST_JOURNAL_MASTER);
					if (questJournalMaster != null) {
						RSInterfaceChild[] children = questJournalMaster.getChildren();
						if (children != null) {
							int lastIndexWithText = 0;
							for (int i = 4; i < (children.length - 3); i++) {
								if (children[i].getText() != null && !children[i].getText().equals("") && !children[i].getText().contains("<str>")) 
									lastIndexWithText = i;
							}
							if (children[lastIndexWithText] != null && !children[lastIndexWithText].getText().equals(""))
							{
								String returnval = children[lastIndexWithText].getText().replaceAll("\\<[^>]*>","");
								closeQuestJournal();
								blockMouse = false;
								return returnval; // cant get this if it's closed ;)
							}
						}
					}
				}
			}
		}
		blockMouse = false;
		return "";
	}

	/**
	 * Closes the quest journal interface.
	 * 
	 * @return true if successfully closed, false if otherwise
	 */
	public static boolean closeQuestJournal() {
		questJournalMaster = Interfaces.get(QUEST_JOURNAL_MASTER);
		if (questJournalMaster != null) {
			RSInterfaceChild children = questJournalMaster.getChild(CHILD_JOURNAL_CLOSE);
			if (children != null) {
				Rectangle rect =  children.getAbsoluteBounds();
				Mouse.clickBox((int)rect.getMinX()+2, (int)rect.getMinY()+2, (int)rect.getMaxX()-2, (int)rect.getMaxY()-2, 1);
				General.sleep(100,120);
			}
		}
		return Interfaces.get(QUEST_JOURNAL_MASTER) == null;
	}

	/**
	 * Scrolls to the quest in the quest list.
	 * 
	 * @param Quest to scroll to
	 * @return true if successful, false if not.
	 */
	public static boolean scrollToQuest(scripts.LanAPI.Constants.Quests quest) {
		GameTab.open(TABS.QUESTS);
		questMaster = Interfaces.get(QUEST_INTERFACE_MASTER);
		if (questMaster != null) {
			RSInterfaceComponent[] scroll = questMaster.getChild(QUEST_INTERFACE_SCROLLBAR).getChildren();
			RSInterfaceChild questChild = questMaster.getChild(quest.getValue());
			if (questChild != null && scroll.length > 0) {
				Mouse.click((int)scroll[0].getAbsolutePosition().getX(), (int)(scroll[0].getAbsolutePosition().getY() + ((questChild.getY() + 15) / SCROLL_Y_PER_PIXEL)), 1);
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the amount of quest points the player has.
	 * 
	 * Does not need to have the quest tab open, it should be available though.
	 * 
	 * @return the amount of quest points or -1 if something went wrong.
	 */
	public static int getAmountOfQuestPoints() {
		questPointChild = Interfaces.get(QUEST_INTERFACE_MASTER, CHILD_QUEST_POINTS);
		if (questPointChild != null) {
			String questPointText = questPointChild.getText();
			if (questPointText != null) {
				questPointText = questPointText.replaceAll("[^\\d]", "");
				try {
					return Integer.parseInt(questPointText);
				} catch (NumberFormatException e) {
					return -1;
				}
			}
		}
		return -1;
	}

	/***
	 *  This function generates a new (Enum) Quests class values.
	 *  Only to be used if jagex updates the questlist/interface!
	 *  It copies the result to the clipboard.
	 */
	public static void _generateQuestIndexNewValues() {
		String finalEnum = "package LanAPI.Constants;\n\npublic enum Quests {\n\n";

		questMaster = Interfaces.get(QUEST_INTERFACE_MASTER);
		if (questMaster != null) {

			RSInterfaceChild[] questChild = questMaster.getChildren();
			for (int i = 0; i < questChild.length; i++) 
			{
				String txt = questChild[i].getText();

				if (txt.equals("") || txt.contains("Quest Points:") || txt.contains("Quest Points:") || txt.contains("Free Quests") || txt.contains("Members' Quests"))
					continue;

				txt = txt.trim();
				txt = txt.replace(" ", "_");
				txt = txt.replace("'", "");
				txt = txt.replace(".", "");
				txt = txt.replace("&", "");
				txt = txt.replace("-", "");
				txt = txt.replace("!", "");
				txt = txt.replace("__", "_");
				txt = txt.toUpperCase();

				finalEnum += "\t"+txt+"("+questChild[i].getIndex()+")";

				finalEnum += (i == (questChild.length-1)) ? ";\n" : ",\n";
			}
		}

		finalEnum += "\n\tprivate int index;\n\tQuests(int index) { this.index = index; }\n\tpublic int getValue() { return index; }\n}";

		StringSelection stringSelection = new StringSelection(finalEnum);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);

		General.println("New (Enum) Quest list generated and copied to clipboard!");
	}

	@Override
	public OVERRIDE_RETURN overrideKeyEvent(KeyEvent e) {
		return OVERRIDE_RETURN.SEND;
	}

	@Override
	public OVERRIDE_RETURN overrideMouseEvent(MouseEvent e) {
		if (blockMouse) {
			e.consume();
			return OVERRIDE_RETURN.DISMISS;
		}
		return OVERRIDE_RETURN.SEND;
	}
}
