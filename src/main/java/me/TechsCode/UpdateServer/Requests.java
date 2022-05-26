package me.TechsCode.UpdateServer;

import bell.oauth.discord.domain.User;
import bell.oauth.discord.main.OAuthBuilder;
import bell.oauth.discord.main.Response;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
            return "An error occurred when receiving the discord redirect";
        }

        User user = builder.getUser();

        Authentication authentication = AuthenticationManager.getAuthentication(request.getRemoteAddr());

        List<DiscordRole> roles = UpdateServer.getDiscordAPI().GetRoles(user.getId());
        if (roles.isEmpty()){
            System.out.println("["+user.getUsername()+"] not found in discord");
            return "<h2>Could Not Authenticate</h2><p>You are not in our support discord</p><h3><a href='http://discord.techscode.com/'>Join Here</a></h3>";
        }

        authentication.setDiscordId(user.getId());
        authentication.setUsername(user.getUsername());
        authentication.setRoles(roles);

        return "<h2>Successfully Authenticated</h2><p>You can now close this window</b>";
    }

    @GetMapping("/{artifact}/download")
    public Object download(@RequestParam(value = "uid") String uid, @PathVariable(value = "artifact") String artifactName, HttpServletRequest request){
        if(uid == null) return "NO-UID";
        if(artifactName == null) return "NO-ARTIFACT-NAME";

        // Get Authentication of User
        Authentication authentication = AuthenticationManager.getAuthenticationByUID(uid);
        if(authentication == null || authentication.getDiscordId() == null){
            return "NOT-AUTHENTICATED";
        }

        AtomicReference<Boolean> isVerified = new AtomicReference<>(false);
        AtomicReference<Boolean> ownsPlugin = new AtomicReference<>(false);

        List<DiscordRole> userRoles = authentication.getRoles();
        userRoles.forEach(userRole -> {
            // Verified Role
            if (Objects.equals(userRole.getId(), "416174015141642240")) isVerified.set(true);

            // Plugin Role
            String pluginName = userRole.getName().toLowerCase().replace(" ", "");
            String artifactNameCompare = artifactName.toLowerCase().replace(" ", "");
            if ( Objects.equals(pluginName, artifactNameCompare) ) ownsPlugin.set(true);

            // Coding Wizard Role
            if (Objects.equals(userRole.getId(), "311178859171282944")) {
                ownsPlugin.set(true);
                isVerified.set(true);
            }
            // Developer Role
            if (Objects.equals(userRole.getId(), "774690360836096062")) {
                ownsPlugin.set(true);
                isVerified.set(true);
            }
            // Assistant Role
            if (Objects.equals(userRole.getId(), "608113993038561325")) {
                ownsPlugin.set(true);
                isVerified.set(true);
            }
        });

        if (!isVerified.get()){
            System.out.println(authentication.getRoles().toString());
            System.out.println("["+authentication.getUsername()+"] could not update because he is not verified");
            return "NOT-VERIFIED";
        }

        if(!ownsPlugin.get()){
            System.out.println("["+authentication.getUsername()+"] could not update because he has not purchased "+artifactName);
            return "NOT-PURCHASED";
        }

        String authorSpigotId = UpdateServer.getConfig().getAuthorSpigotId();

        // Use Newest Version
        // String version = UpdateServer.artifacts.filterByName(artifactName).getBestVersion().get();
        String version = SpigotMC.getReleasedVersion(authorSpigotId, artifactName);

        if(version == null){
            return "Could not retrieved latest version from SpigotMC";
        }

        Optional<Artifact> artifact = UpdateServer.artifacts
                .filterByName(artifactName)
                .filterByVersion(version)
                .getOldestArtifact();

        if(!artifact.isPresent()){
            System.out.println("Could not get desired artifact");
            return "NO-ARTIFACT";
        }

        try {
            //AuthenticationManager.revokeAuthentication(authentication);

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
