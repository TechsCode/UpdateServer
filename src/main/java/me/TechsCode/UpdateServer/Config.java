package me.TechsCode.UpdateServer;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Config {

    private File file;
    private JSONObject root;

    public Config() {
        this.file = new File("config.json");

        if(!file.exists()){
            try {
                InputStream src = Config.class.getResourceAsStream("/deployment.json");
                Files.copy(src, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

            JSONParser jsonParser = new JSONParser();
            root = (JSONObject) jsonParser.parse(json);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public MySQLCredentials getMySQLCredentials(){
        JSONObject mysql = (JSONObject) root.get("mysql");

        String host = (String) mysql.get("host");
        String port = (String) mysql.get("port");
        String database = (String) mysql.get("database");
        String username = (String) mysql.get("username");
        String password = (String) mysql.get("password");

        return new MySQLCredentials(host, port, database, username, password);
    }

    public DiscordCredentials getDiscordCredentials(){
        JSONObject discord = (JSONObject) root.get("discord");

        String clientId = (String) discord.get("clientId");
        String clientSecret = (String) discord.get("clientSecret");

        return new DiscordCredentials(clientId, clientSecret);
    }

    public SpigotAPICredentials getSpigotAPICredentials(){
        JSONObject spigotApi = (JSONObject) root.get("spigotApi");

        String url = (String) spigotApi.get("url");
        String token = (String) spigotApi.get("token");

        return new SpigotAPICredentials(url, token);
    }

    public String getHost(){
        return (String) root.get("host");
    }

    public String getPort(){
        return (String) root.get("port");
    }

    public String getAuthorSpigotId(){
        return (String) root.get("authorSpigotId");
    }

    public boolean isConfigured(){
        return (boolean) root.get("configured");
    }

    public class MySQLCredentials {

        private String host, port, database, username, password;

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

    public class DiscordCredentials {

        private String clientId, clientSecret;

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

    public class SpigotAPICredentials {

        private String url, token;

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
