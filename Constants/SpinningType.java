package scripts.LanAPI.Constants;

/**
 * @author Laniax
 *
 */
public enum SpinningType {
	BALL_OF_WHOOL(96),
	BOW_STRING(91),
	MAGIC_AMULET_STRING(103),
	CBOW_STRING_TREE_ROOTS(117),
	CBOW_STRING_SINEW(110),
	ROPE(124);

	private int index;
	SpinningType(int index) { this.index = index; }
	public int getValue() { return index; }
}