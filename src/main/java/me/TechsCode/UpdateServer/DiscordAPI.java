package me.TechsCode.UpdateServer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DiscordAPI {

    private final String apiUrl;
    private final String apiKey;

    public DiscordAPI(String url, String key){
        this.apiUrl = url;
        this.apiKey = key;
    }

    private JsonObject MakeObjectRequest(String endpoint){
        JsonObject response = new JsonObject();
        try{
            URL url = new URL(apiUrl +"/api/"+ apiKey +"/"+endpoint);

            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder responseText = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    responseText.append(responseLine.trim());
                }
                response = new Gson().fromJson(responseText.toString(), JsonObject.class);
                response.addProperty("statusCode", con.getResponseCode());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }
    private JsonArray MakeArrayRequest(String endpoint){
        JsonArray response = new JsonArray();
        try{
            URL url = new URL(apiUrl +"/api/"+ apiKey +"/"+endpoint);

            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder responseText = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    responseText.append(responseLine.trim());
                }
                response = new Gson().fromJson(responseText.toString(), JsonArray.class);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    public boolean HasRole(String memberId, String roleId){
        String url = "guild/member/"+memberId+"/roles/"+roleId;
        JsonObject response = MakeObjectRequest(url);
        return response.get("hasRole").getAsBoolean();
    }
    public boolean HasRoles(String memberId, String[] roleIds, Boolean RequireOne){
        boolean HasRoles = false;
        for (String roleId : roleIds) {
            String url = "guild/member/"+memberId+"/roles/"+roleId;
            JsonObject response = MakeObjectRequest(url);
            HasRoles = response.get("hasRole").getAsBoolean();
            if (RequireOne){
                if (HasRoles){
                    return true;
                }
            }else{
                if (!HasRoles){
                    return false;
                }
            }
        }
        return HasRoles;
    }
    public List<DiscordRole> GetRoles(String memberId){
        String url = "guild/member/"+memberId+"/roles";
        JsonArray response = MakeArrayRequest(url);
        List<DiscordRole> roles = new ArrayList<>();
        response.forEach(role -> {
            roles.add(new DiscordRole(role.getAsJsonObject()));
        });
        return roles;
    }
    public DiscordMember GetUserInfo(String memberId){
        String url = "guild/member/"+memberId+"/info";
        JsonObject response = MakeObjectRequest(url);
        return new DiscordMember(response);
    }

}
