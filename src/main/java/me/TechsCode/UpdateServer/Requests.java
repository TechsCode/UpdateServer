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
import java.util.Arrays;
import java.util.Map;

@RestController
public class Requests {

    public OAuthBuilder getBuilder(){
        Config config = UpdateServer.getConfig();

        return new OAuthBuilder(config.getDiscordCredentials().getClientId(), config.getDiscordCredentials().getClientSecret())
                .setScopes(new String[]{"email", "identify"})
                .setRedirectURI(config.getHost()+"/discord");
    }

    @GetMapping("/{artifact}/version")
    public String retrieveVersion(@PathVariable(value = "artifact") String artifactName){
        Artifact artifact = UpdateServer.getArtifact(artifactName);

        if(artifact == null){
            return "Could not find artifact with the name "+artifactName;
        }

        return artifact.getVersion();
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

        return "<h2>Successfully Authenticated</h2><p>The download of the update should start very soon.</b>";
    }

    @GetMapping("/{artifact}/download")
    public Object requestUpdate(@RequestParam(value = "uid") String uid, @PathVariable(value = "artifact") String artifactName, HttpServletRequest request){
        if(uid == null){
            return "NO-UID";
        }

        if(artifactName == null){
            return "NO-ARTIFACT-NAME";
        }

        Artifact artifact = UpdateServer.getArtifact(artifactName);

        if(artifact == null){
            return "NO-ARTIFACT";
        }

        Authentication authentication = AuthenticationManager.getAuthenticationByUID(uid);

        if(authentication == null || authentication.getDiscordId() == null){
            return "NOT-AUTHENTICATED";
        }

        String userId = UpdateServer.getVerifiedSpigotId(authentication.getDiscordId());

        if(userId == null){
            return "NOT-VERIFIED";
        }

        String[] purchasedArtifacts = UpdateServer.getPurchasedArtifacts(userId);

        if(!userId.equals(UpdateServer.getConfig().getAuthorSpigotId()) && !Arrays.asList(purchasedArtifacts).contains(artifact.getName())){
            return "NOT-PURCHASED";
        }

        // Try to determine file's content type
        String contentType = request.getServletContext().getMimeType(artifact.getFile().getAbsolutePath());

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        try {
            Resource resource = new UrlResource(artifact.getFile().toURI());

            System.out.println("["+authentication.getUsername()+"] is now updating "+artifact.getName());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + artifactName + ".jar\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            e.printStackTrace();

            return "An error occurred when downloading the artifact";
        }
    }
}
