package me.TechsCode.UpdateServer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ArtifactList extends ArrayList<Artifact> {

    public ArtifactList filterByName(String artifactName){
        return stream().filter(x -> x.getName().equalsIgnoreCase(artifactName)).collect(Collectors.toCollection(ArtifactList::new));
    }

    public ArtifactList filterByVersion(String version){
        return stream().filter(x -> x.getVersion().equalsIgnoreCase(version)).collect(Collectors.toCollection(ArtifactList::new));
    }

    public Optional<String> getBestVersion(){
        return stream().map(Artifact::getVersion).max(Comparator.comparing(a -> Integer.parseInt(a.replace(".", ""))));
    }

    public Optional<Artifact> getOldestArtifact(){
        return stream().min(Comparator.comparing(Artifact::getBuild));
    }

}
