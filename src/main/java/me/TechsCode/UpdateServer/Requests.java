package me.TechsCode.UpdateServer;

import bell.oauth.discord.domain.User;
import bell.oauth.discord.main.OAuthBuilder;
import bell.oauth.discord.main.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class Requests {

    public OAuthBuilder getBuilder(){
        Config config = UpdateServer.getConfig();

        return new OAuthBuilder(config.getDiscordCredentials().getClientId(), config.getDiscordCredentials().getClientSecret())
                .setScopes(new String[]{"identify"})
                .setRedirectURI(config.getHost()+"/discord");
    }

    @RequestMapping("/")
    public String index() {
        return "Im a happy server that updates your plugins :)";
    }

    @GetMapping("/{artifact}/version")
    public String retrieveVersion(@PathVariable(value = "artifact") String artifactName){
        Optional<String> bestVersion = UpdateServer.artifacts.filterByName(artifactName).getBestVersion();

        return bestVersion.orElseGet(() -> "Could not find artifact with the name " + artifactName);
    }

    @GetMapping("/authenticate")
    public Object authenticate(@RequestParam(value = "uid") String uid, HttpServletRequest request){
        if(uid == null){
            return "NO-UID";
        }

        AuthenticationManager.newAuthentication(request.getRemoteAddr(), uid);

        String url = getBuilder().getAuthorizationUrl(null);

        return new RedirectView(url);
    }

    @GetMapping("/discord")
    public String authenticationResponse(@RequestParam(value = "code") String code, HttpServletRequest request){
        OAuthBuilder builder = getBuilder();
        Response response = builder.exchange(code);

        if(response == Response.ERROR){
            return "An error occured when receiving the discord redirect";
        }

        User user = builder.getUser();

        Authentication authentication = AuthenticationManager.getAuthentication(request.getRemoteAddr());
        authentication.setDiscordId(user.getId());
        authentication.setUsername(user.getUsername());

        System.out.println("["+user.getUsername()+"] has been authenticated");

        return "<h2>Successfully Authenticated</h2><p>You can now close this window</b>";
    }

    @GetMapping("/{artifact}/download")
    public Object download(@RequestParam(value = "uid") String uid, @PathVariable(value = "artifact") String artifactName, HttpServletRequest request) {
        if(artifactName == null) return "NO-ARTIFACT-NAME";

        Optional<String> newestVersion = UpdateServer.artifacts.filterByName(artifactName).getBestVersion();

        Optional<Artifact> artifact = UpdateServer.artifacts
                .filterByName(artifactName)
                .filterByVersion(newestVersion.get())
                .getOldestArtifact();

        if(artifact.isPresent()){
            return download(uid, artifactName, artifact.get().getBuild()+"", request);
        } else {
            return "NO-ARTIFACT";
        }
    }

    @GetMapping("/{artifact}/download")
    public Object download(@RequestParam(value = "uid") String uid, @PathVariable(value = "artifact") String artifactName, @PathVariable(value = "build") String build, HttpServletRequest request){
        if(uid == null) return "NO-UID";
        if(artifactName == null) return "NO-ARTIFACT-NAME";

        // Get Authentication of User
        Authentication authentication = AuthenticationManager.getAuthenticationByUID(uid);
        if(authentication == null || authentication.getDiscordId() == null){
            return "NOT-AUTHENTICATED";
        }

        // Get Spigot UserId from Database (which is only possible if he is verified)
        String userId = UpdateServer.getVerifiedSpigotId(authentication.getDiscordId());
        if(userId == null){
            System.out.println("["+authentication.getUsername()+"] could not update because he is not verified");
            return "NOT-VERIFIED";
        }

        // Check if he has purchased the plugin
        List<String> purchasedArtifacts = Arrays.stream(UpdateServer.getPurchasedArtifacts(userId))
                .map(name -> name.replace(" ", "").toLowerCase())
                .collect(Collectors.toList());

        boolean isAuthor = userId.equals(UpdateServer.getConfig().getAuthorSpigotId());
        boolean hasPurchased = purchasedArtifacts.contains(artifactName.toLowerCase());

        if(!isAuthor && !hasPurchased){
            System.out.println("["+authentication.getUsername()+"] could not update because he has not purchased "+artifactName);
            return "NOT-PURCHASED";
        }

        Optional<Artifact> artifact = UpdateServer.artifacts
                .filterByName(artifactName)
                .getArtifactWithBuildNumber(Integer.parseInt(build));

        if(!artifact.isPresent()){
            System.out.println("Could not get desired artifact");
            return "NO-ARTIFACT";
        }

        try {
            AuthenticationManager.revokeAuthentication(authentication);

            Resource resource = new UrlResource(artifact.get().getFile().toURI());

            System.out.println("["+authentication.getUsername()+"] is now updating ["+artifact.get().getName()+"] to ["+artifact.get().getVersion()+" build-"+artifact.get().getBuild()+"]");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + artifactName + ".jar\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            e.printStackTrace();

            return "An error occurred when downloading the artifact";
        }
    }
}
