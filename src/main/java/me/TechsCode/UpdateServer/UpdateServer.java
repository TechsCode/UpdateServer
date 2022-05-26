package me.TechsCode.UpdateServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class UpdateServer {

    private static Config config;
    public static ArtifactList artifacts;
    private static SpigotApi spigotAPIClient;
    private static DiscordAPI discordAPI;

    public static void main(String[] args) {
        config = new Config();

        if (!config.isConfigured()) {
            System.out.println("Please configure the config.json first!");
            return;
        }

        new ArtifactManager() {
            @Override
            public void onRetrieve(ArtifactList artifacts) {
                UpdateServer.artifacts = artifacts;
            }
        };

        discordAPI = new DiscordAPI(config.getDiscordAPICredentials().getUrl(), config.getDiscordAPICredentials().getKey());
        spigotAPIClient = new SpigotApi(config.getSpigotAPICredentials(), config.getSpigetAPICredentials());

        SpringApplication app = new SpringApplication(UpdateServer.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", config.getPort()));

        app.run(args);
    }

    public static SpigotApi getSpigotAPIClient() {
        return spigotAPIClient;
    }

    public static DiscordAPI getDiscordAPI() {
        return discordAPI;
    }

    public static Config getConfig() {
        return config;
    }
}
