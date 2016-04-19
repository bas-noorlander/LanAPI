package scripts.lanapi.core.system;

/**
 * @author Laniax
 */
public class NotificationPreferences {

    // Default values.
    private boolean onPrivateMessage = true;
    private boolean onServerMessage = false;
    private boolean onChatMessage = false;
    private boolean onClanMessage = false;
    private boolean onDuelRequest = true;
    private boolean onTradeRequest = true;
    private boolean onBreakStart = true;
    private boolean onBreakEnd = true;
    private boolean onSkillLevelUp = true;

    public boolean isOnPrivateMessage() {
        return onPrivateMessage;
    }

    public void setOnPrivateMessage(boolean value) {
        this.onPrivateMessage = value;
    }

    public boolean isOnServerMessage() {
        return onServerMessage;
    }

    public void setOnServerMessage(boolean value) {
        this.onServerMessage = value;
    }

    public boolean isOnChatMessage() {
        return onChatMessage;
    }

    public void setOnChatMessage(boolean value) {
        this.onChatMessage = value;
    }

    public boolean isOnClanMessage() {
        return onClanMessage;
    }

    public void setOnClanMessage(boolean value) {
        this.onClanMessage = value;
    }

    public boolean isOnDuelRequest() {
        return onDuelRequest;
    }

    public void setOnDuelRequest(boolean value) {
        this.onDuelRequest = value;
    }

    public boolean isOnTradeRequest() {
        return onTradeRequest;
    }

    public void setOnTradeRequest(boolean value) {
        this.onTradeRequest = value;
    }

    public boolean isOnBreakStart() {
        return onBreakStart;
    }

    public void setOnBreakStart(boolean value) {
        this.onBreakStart = value;
    }

    public boolean isOnBreakEnd() {
        return onBreakEnd;
    }

    public void setOnBreakEnd(boolean value) {
        this.onBreakEnd = value;
    }

    public boolean isOnSkillLevelUp() {
        return onSkillLevelUp;
    }

    public void setOnSkillLevelUp(boolean value) {
        this.onSkillLevelUp = value;
    }
}
