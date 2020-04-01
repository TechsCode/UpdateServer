package me.TechsCode.UpdateServer;

import me.TechsCode.SpigotAPI.client.SpigotAPIClient;
import me.TechsCode.SpigotAPI.client.objects.Purchase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class UpdateServer {

    private static Config config;
    private static ArtifactManager artifactManager;
    private static VerificationStorage verificationStorage;
    private static SpigotAPIClient spigotAPIClient;

    public static void main(String[] args){
        config = new Config();

        if(!config.isConfigured()){
            System.out.println("Please configure the config.json first!");
            return;
        }

        artifactManager = new ArtifactManager();
        verificationStorage = new VerificationStorage(config.getMySQLCredentials());
        spigotAPIClient = new SpigotAPIClient(config.getSpigotAPICredentials().getUrl(), config.getSpigotAPICredentials().getToken());

        SpringApplication app = new SpringApplication(UpdateServer.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", config.getPort()));

        app.run(args);

        System.out.println("Startup Completed");
    }

    public static Artifact getArtifact(String name){
        return artifactManager.getArtifacts().stream()
                .filter(artifact -> artifact.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
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
