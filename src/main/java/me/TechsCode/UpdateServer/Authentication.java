package me.TechsCode.UpdateServer;

public class Authentication {

    private String uid;

    private String discordId, username;

    public Authentication(String uid) {
        this.uid = uid;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public String getDiscordId() {
        return discordId;
    }

    public String getUsername() {
        return username;
    }
}
