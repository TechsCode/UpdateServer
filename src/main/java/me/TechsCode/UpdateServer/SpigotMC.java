package me.TechsCode.UpdateServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.StreamSupport;

public class SpigotMC {

    public static String getReleasedVersion(String spigotUserId, String pluginName){
        try {
            String json = IOUtils.toString(new URI("https://api.spigotmc.org/simple/0.1/index.php?action=getResourcesByAuthor&id=" + spigotUserId+"&"+ UUID.randomUUID().toString()), StandardCharsets.UTF_8);

            JsonArray jsonArray = (JsonArray) JsonParser.parseString(json);

            for(JsonElement element : jsonArray){
                JsonObject jsonObject = (JsonObject) element;
                String name = jsonObject.get("title").getAsString().replace(" ", "");
                String version = jsonObject.get("current_version").getAsString();

                if(name.equalsIgnoreCase(pluginName)){
                    return version;
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }

}
