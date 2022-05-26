package me.TechsCode.UpdateServer.spiget.lists;

import me.TechsCode.UpdateServer.spiget.objects.SpigetUpdate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class SpigetUpdatesList extends ArrayList<SpigetUpdate> {

    public SpigetUpdatesList(int initialCapacity) {
        super(initialCapacity);
    }

    public SpigetUpdatesList() {}

    public SpigetUpdatesList(Collection<? extends SpigetUpdate> c) {
        super(c);
    }

    public SpigetUpdatesList id(int id){
        return stream().filter(update -> update.getId() == id).collect(Collectors.toCollection(SpigetUpdatesList::new));
    }

    public SpigetUpdatesList title(String title){
        return stream().filter(update -> update.getTitle().equals(title)).collect(Collectors.toCollection(SpigetUpdatesList::new));
    }

    public SpigetUpdatesList dateBefore(int date){
        return stream().filter(update -> date < update.getDate()).collect(Collectors.toCollection(SpigetUpdatesList::new));
    }

    public SpigetUpdatesList dateAfter(int date){
        return stream().filter(update -> date > update.getDate()).collect(Collectors.toCollection(SpigetUpdatesList::new));
    }

    public SpigetUpdatesList likesGreater(int likes){
        return stream().filter(update -> likes > update.getLikes()).collect(Collectors.toCollection(SpigetUpdatesList::new));
    }

    public SpigetUpdatesList likesLess(int likes){
        return stream().filter(update -> likes < update.getLikes()).collect(Collectors.toCollection(SpigetUpdatesList::new));
    }

}
