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

public abstract class ArtifactManager extends Thread {

    private File importFolder, artifactsFolder;

    public ArtifactManager() {
        this.importFolder = new File("import");
        this.artifactsFolder = new File("artifacts");

        importFolder.mkdir();

        start();
    }

    public abstract void onRetrieve(ArtifactList artifacts);

    @Override
    public void run(){
        while (true){
            for(File file : Objects.requireNonNull(importFolder.listFiles())){
                if(!file.getName().endsWith(".jar")) continue;

                try {
                    ZipFile zipFile = new ZipFile(file.getAbsolutePath());
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();

                    while (entries.hasMoreElements()){
                        ZipEntry e = entries.nextElement();

                        if (e.getName().equals("plugin.yml")){
                            InputStream stream = zipFile.getInputStream(e);

                            List<String> lines = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines().collect(Collectors.toList());

                            Optional<String> name = lines.stream()
                                    .filter(line -> line.startsWith("name"))
                                    .map(line -> line.replace("name: ", "")).findFirst();

                            Optional<String> version = lines.stream()
                                    .filter(line -> line.startsWith("version"))
                                    .map(line -> line.replace("version: ", "")).findFirst();

                            Optional<Integer> build = lines.stream()
                                    .filter(line -> line.startsWith("build"))
                                    .map(line -> line.replace("build: ", ""))
                                    .map(Integer::parseInt).findFirst();

                            if(name.isPresent() && version.isPresent() && build.isPresent()){
                                File destination = new File(artifactsFolder.getAbsolutePath()+"/"+name.get()+"/"+version.get()+"/"+build.get()+"/"+file.getName());
                                FileUtils.moveFile(file, destination);
                            }
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            ArtifactList artifacts = new ArtifactList();
            for(File artifactsFolder : Objects.requireNonNull(artifactsFolder.listFiles())){
                for(File versionFolder : Objects.requireNonNull(artifactsFolder.listFiles())){
                    for(File buildFolder : Objects.requireNonNull(versionFolder.listFiles())){
                        for(File jarFile : Objects.requireNonNull(buildFolder.listFiles())){
                            String name = artifactsFolder.getName();
                            String version = versionFolder.getName();
                            int build = Integer.parseInt(buildFolder.getName());

                            artifacts.add(new Artifact(name, version, build, jarFile));
                        }
                    }
                }
            }

            onRetrieve(artifacts);

            try {
                sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
