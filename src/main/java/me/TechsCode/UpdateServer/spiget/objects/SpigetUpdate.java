package me.TechsCode.UpdateServer.spiget.objects;

import me.TechsCode.SpigotAPI.data.Resource;

public class SpigetUpdate {

    private final Resource resource;
    private final String title, description;
    private final int date, likes, id;

    public SpigetUpdate(Resource resource, String title, String description, int date, int likes, int id) {
        this.resource = resource;
        this.title = title;
        this.description = description;
        this.date = date;
        this.likes = likes;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Resource getResource() {
        return resource;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getDate() {
        return date;
    }

    public int getLikes() {
        return likes;
    }

}
