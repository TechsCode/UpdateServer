package me.TechsCode.UpdateServer;

import java.io.File;

public class Artifact {

    private String name, version;
    private int build;
    private File file;

    public Artifact(String name, String version, int build, File file) {
        this.name = name;
        this.version = version;
        this.build = build;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public int getBuild() {
        return build;
    }

    public File getFile() {
        return file;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Artifact && hashCode() == obj.hashCode();
    }
}
