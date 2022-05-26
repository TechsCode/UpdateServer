package me.TechsCode.UpdateServer;

import com.google.gson.JsonObject;

public class DiscordMember {

    private final String guildId, userID, nickname, displayName;
    private final long joinedTimestamp, lastMessageChannelID, premiumSinceTimestamp;
    private final boolean deleted;

    public DiscordMember(JsonObject jsonObject){
        this.guildId = jsonObject.get("guildID").getAsString();
        this.userID = jsonObject.get("userID").getAsString();
        this.nickname = jsonObject.get("nickname").getAsString();
        this.displayName = !jsonObject.get("displayName").isJsonNull() ? jsonObject.get("displayName").getAsString() : null;
        this.joinedTimestamp = !jsonObject.get("joinedTimestamp").isJsonNull() ? jsonObject.get("joinedTimestamp").getAsLong(): 0;
        this.lastMessageChannelID = !jsonObject.get("lastMessageChannelID").isJsonNull() ? jsonObject.get("lastMessageChannelID").getAsLong(): 0;
        this.premiumSinceTimestamp = !jsonObject.get("premiumSinceTimestamp").isJsonNull() ? jsonObject.get("premiumSinceTimestamp").getAsLong(): 0;
        this.deleted = jsonObject.get("deleted").getAsBoolean();
    }

    public String getGuildId() {
        return guildId;
    }

    public String getUserID() {
        return userID;
    }

    public String getNickname() {
        return nickname;
    }

    public String getDisplayName() {
        return displayName;
    }

    public long getJoinedTimestamp() {
        return joinedTimestamp;
    }

    public long getLastMessageChannelID() {
        return lastMessageChannelID;
    }

    public long getPremiumSinceTimestamp() {
        return premiumSinceTimestamp;
    }

    public boolean isDeleted() {
        return deleted;
    }

}
