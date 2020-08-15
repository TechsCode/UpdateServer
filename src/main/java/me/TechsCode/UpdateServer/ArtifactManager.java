package me.TechsCode.UpdateServer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ArtifactManager extends Thread {

    private final File root = new File("artifacts");

    private List<Artifact> artifacts;

    public ArtifactManager() {
        this.artifacts = new ArrayList<>();
        start();
    }

    @Override
    public void run(){
        while (true){
            List<Artifact> artifacts = new ArrayList<>();

            for(File file : Objects.requireNonNull(root.listFiles())){
                if(!file.getName().endsWith(".jar")) continue;

                try {
                    ZipFile zipFile = new ZipFile(file.getAbsolutePath());
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();

                    while (entries.hasMoreElements()){
                        ZipEntry e = entries.nextElement();

                        if (e.getName().equals("plugin.yml")){
                            InputStream stream = zipFile.getInputStream(e);

                            Optional<String> version = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines()
                                    .filter(line -> line.startsWith("version"))
                                    .map(line -> line.replace("version: ", ""))
                                    .findAny();

                            version.ifPresent(s -> artifacts.add(new Artifact(file.getName().replace(".jar", ""), s, file)));
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            artifacts.stream().filter(artifact -> !this.artifacts.contains(artifact))
                    .forEach(artifact -> System.out.println("[System] Loaded "+artifact.getName()+" on version "+artifact.getVersion()));

            artifacts.stream().filter(artifact -> this.artifacts.contains(artifact) && !this.artifacts.get(this.artifacts.indexOf(artifact)).getVersion().equals(artifact.getVersion()))
                    .forEach(artifact -> System.out.println("[System] Updated "+artifact.getName()+" to "+artifact.getVersion()));

            this.artifacts = artifacts;

            try {
                sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Artifact> getArtifacts(){
        return artifacts;
    }


}
