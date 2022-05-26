package me.TechsCode.UpdateServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.TechsCode.SpigotAPI.data.Purchase;
import me.TechsCode.SpigotAPI.data.Resource;
import me.TechsCode.SpigotAPI.data.lists.PurchasesList;
import me.TechsCode.UpdateServer.spiget.lists.SpigetUpdatesList;
import me.TechsCode.UpdateServer.spiget.objects.SpigetUpdate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SpigotApi {

    private final Config.SpigotAPICredentials spigotCredentials;
    private final Config.SpigetAPICredentials spigetCredentials;

    private final SpigetUpdatesList spigetUpdates;
    private final PurchasesList spigotPurchases;

    public SpigotApi(Config.SpigotAPICredentials spigotCredentials, Config.SpigetAPICredentials spigetCredentials) {
        this.spigotCredentials = spigotCredentials;
        this.spigetCredentials = spigetCredentials;

        this.spigetUpdates = new SpigetUpdatesList();
        this.spigotPurchases = new PurchasesList();

        fetchPurchases();
        fetchUpdates();
    }

    private void fetchUpdates(){
        this.spigetUpdates.clear();

        System.out.println("[UpdateServer] Fetching updates...");

        Resource.getAllResources().forEach(resource -> {
            String resourceId = resource.getId().split("\\.")[1];
            String data = makeGetRequest(spigetCredentials.getUrl()+"resources/"+resourceId+"/updates");

            if (data.equals("error")) {
                System.out.println("[UpdateServer] Error fetching updates for " + resource.getName() + ".");
                return;
            }

            JsonArray updates = JsonParser.parseString(data).getAsJsonArray();

            updates.forEach(update -> {
                JsonObject updateObject = update.getAsJsonObject();
                this.spigetUpdates.add(new SpigetUpdate(
                        resource,
                        updateObject.get("title").getAsString(),
                        updateObject.get("description").getAsString(),
                        updateObject.get("date").getAsInt(),
                        updateObject.get("likes").getAsInt(),
                        updateObject.get("id").getAsInt()
                ));
            });

            System.out.println("[UpdateServer] Fetched " + spigetUpdates.size() + " updates for " + resource.getName() + ".");
        });

        System.out.println("[UpdateServer] Fetched " + spigetUpdates.size() + " updates.");
    }

    public SpigetUpdatesList getUpdates(){
        return spigetUpdates;
    }

    private void fetchPurchases(){
        PurchasesList tempPurchases = new PurchasesList();

        System.out.println("[UpdateServer] Fetching purchases...");

        Resource.getAllResources().forEach(resource -> {
            String resourceId = resource.getId();
            String data = makeGetRequest(spigotCredentials.getUrl()+"purchases?token=" + spigotCredentials.getToken()+"&resourceId="+resourceId);

            if (data.equals("error")) {
                System.out.println("[UpdateServer] Error fetching purchases for " + resource.getName() + ".");
                return;
            }

            JsonArray purchases = JsonParser.parseString(data).getAsJsonObject().get("data").getAsJsonArray();

            purchases.forEach(purchase -> {
                JsonObject updateObject = purchase.getAsJsonObject();

                tempPurchases.add(new Purchase(updateObject));
            });

            System.out.println("[UpdateServer] Fetched " + tempPurchases.size() + " purchases for " + resource.getName() + ".");
        });

        this.spigotPurchases.clear();
        this.spigotPurchases.addAll(tempPurchases);

        System.out.println("[UpdateServer] Fetched " + spigotPurchases.size() + " purchases.");
    }

    public PurchasesList getPurchases(){
        return spigotPurchases;
    }

    private String makeGetRequest(String requestUrl){
        String response = "error";
        try{
            URL url = new URL(requestUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Content-Type", "application/json");

            int status = con.getResponseCode();
            if(status == 200){
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();

                return content.toString();
            }else{
                System.out.println("Error fetching `"+url+"`: " + status);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

}
