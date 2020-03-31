package me.TechsCode.UpdateServer;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArtifactManager {

    private File artifactsFolder;

    public ArtifactManager() {
        this.artifactsFolder = new File("artifacts");

        artifactsFolder.mkdir();
    }

    public List<Artifact> getArtifacts(){
        return Arrays.stream(artifactsFolder.listFiles())
                .filter(file -> file.getName().endsWith(".jar") && file.getName().contains("-"))
                .map(file -> {
                    String name = file.getName().split("-")[0];
                    String version = file.getName().split("-")[1].replace(".jar", "");

                    return new Artifact(name, version, file);
                })
                .collect(Collectors.toList());
    }


}
