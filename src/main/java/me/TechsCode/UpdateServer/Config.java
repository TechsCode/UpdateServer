package me.TechsCode.UpdateServer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Config {

    private File file;
    private JsonObject root;

    public Config() {
        this.file = new File("config.json");

        if(!file.exists()){
            try {
                InputStream src = Config.class.getResourceAsStream("/config.json");
                Files.copy(src, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

            root = (JsonObject) JsonParser.parseString(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MySQLCredentials getMySQLCredentials(){
        JsonObject mysql = root.get("mysql").getAsJsonObject();

        String host = mysql.get("host").getAsString();
        String port = mysql.get("port").getAsString();
        String database = mysql.get("database").getAsString();
        String username = mysql.get("username").getAsString();
        String password = mysql.get("password").getAsString();

        return new MySQLCredentials(host, port, database, username, password);
    }

    public DiscordCredentials getDiscordCredentials(){
        JsonObject discord = root.get("discord").getAsJsonObject();

        String clientId = discord.get("clientId").getAsString();
        String clientSecret = discord.get("clientSecret").getAsString();

        return new DiscordCredentials(clientId, clientSecret);
    }

    public SpigotAPICredentials getSpigotAPICredentials(){
        JsonObject spigotApi = root.get("spigotApi").getAsJsonObject();

        String url = spigotApi.get("url").getAsString();
        String token = spigotApi.get("token").getAsString();

        return new SpigotAPICredentials(url, token);
    }

    public String getHost(){
        return root.get("host").getAsString();
    }

    public String getPort(){
        return root.get("port").getAsString();
    }

    public String getAuthorSpigotId(){
        return root.get("authorSpigotId").getAsString();
    }

    public boolean isConfigured(){
        return root.get("configured").getAsBoolean();
    }

    public static class MySQLCredentials {

        private final String host;
        private final String port;
        private final String database;
        private final String username;
        private final String password;

        public MySQLCredentials(String host, String port, String database, String username, String password) {
            this.host = host;
            this.port = port;
            this.database = database;
            this.username = username;
            this.password = password;
        }

        public String getHost() {
            return host;
        }

        public String getPort() {
            return port;
        }

        public String getDatabase() {
            return database;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    public static class DiscordCredentials {

        private final String clientId, clientSecret;

        public DiscordCredentials(String clientId, String clientSecret) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        public String getClientId() {
            return clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }
    }

    public static class SpigotAPICredentials {

        private final String url, token;

        public SpigotAPICredentials(String url, String token) {
            this.url = url;
            this.token = token;
        }

        public String getUrl() {
            return url;
        }

        public String getToken() {
            return token;
        }
    }

}
