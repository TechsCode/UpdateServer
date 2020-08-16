package me.TechsCode.UpdateServer;

import me.TechsCode.SpigotAPI.client.SpigotAPIClient;
import me.TechsCode.SpigotAPI.client.objects.Purchase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class UpdateServer {

    private static Config config;
    public static ArtifactList artifacts;
    private static VerificationStorage verificationStorage;
    private static SpigotAPIClient spigotAPIClient;

    public static void main(String[] args){
        config = new Config();

        if(!config.isConfigured()){
            System.out.println("Please configure the config.json first!");
            return;
        }

        new ArtifactManager(){
            @Override
            public void onRetrieve(ArtifactList artifacts) {
                UpdateServer.artifacts = artifacts;
            }
        };

        verificationStorage = new VerificationStorage(config.getMySQLCredentials());
        spigotAPIClient = new SpigotAPIClient(config.getSpigotAPICredentials().getUrl(), config.getSpigotAPICredentials().getToken());

        SpringApplication app = new SpringApplication(UpdateServer.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", config.getPort()));

        app.run(args);
    }

    public static String getVerifiedSpigotId(String discordId){
        return verificationStorage.getSpigotUserId(discordId);
    }

    public static String[] getPurchasedArtifacts(String spigotUserId){
        return spigotAPIClient.getPurchases().userId(spigotUserId).getStream().map(Purchase::getResourceName).toArray(String[]::new);
    }

    public static Config getConfig() {
        return config;
    }
}
