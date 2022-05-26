package me.TechsCode.UpdateServer;

import java.util.HashMap;

public class AuthenticationManager {

    private static HashMap<String, Authentication> authentications = new HashMap<>();
    
    public static void newAuthentication(String ipAddr, String uid){
        authentications.put(ipAddr, new Authentication(uid, ipAddr));
    }

    public static void revokeAuthentication(Authentication authentication){
        authentications.remove(authentication.getIpAddr());
    }

    public static Authentication getAuthentication(String ipAddr){
        return authentications.getOrDefault(ipAddr, null);
    }

    public static Authentication getAuthenticationByUID(String uid){
        return authentications.values().stream().filter(authentication -> authentication.getUid().equalsIgnoreCase(uid)).findFirst().orElse(null);
    }
}
