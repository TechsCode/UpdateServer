package me.TechsCode.UpdateServer;

import com.google.gson.JsonObject;

public class DiscordRole {

    private final String guild, id, name;
    private final long color, rawPosition, permissions, createdTimestamp;
    private final boolean hoist, managed, mentionable, deleted;

    DiscordRole(JsonObject jsonObject){
        this.guild = jsonObject.get("guild").getAsString();
        this.id = jsonObject.get("id").getAsString();
        this.name = jsonObject.get("name").getAsString();
        this.color = jsonObject.get("color").getAsLong();
        this.rawPosition = jsonObject.get("rawPosition").getAsLong();
        this.permissions = jsonObject.get("permissions").getAsLong();
        this.createdTimestamp = jsonObject.get("createdTimestamp").getAsLong();
        this.hoist = jsonObject.get("hoist").getAsBoolean();
        this.managed = jsonObject.get("managed").getAsBoolean();
        this.mentionable = jsonObject.get("mentionable").getAsBoolean();
        this.deleted = jsonObject.get("deleted").getAsBoolean();
    }

    public String getGuild() {
        return guild;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getColor() {
        return color;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public long getPermissions() {
        return permissions;
    }

    public long getRawPosition() {
        return rawPosition;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isHoist() {
        return hoist;
    }

    public boolean isManaged() {
        return managed;
    }

    public boolean isMentionable() {
        return mentionable;
    }
}
