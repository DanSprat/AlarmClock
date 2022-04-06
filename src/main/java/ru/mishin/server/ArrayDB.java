package ru.mishin.server;

import ru.mishin.database.IDataBase;
import ru.mishin.database.models.Event;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ArrayDB implements IDataBase {

    private static AtomicInteger currentID = new AtomicInteger(0);
    private CopyOnWriteArrayList<Event> events;
    public ArrayDB() {
        events = new CopyOnWriteArrayList<>();
    }

    @Override
    public List<Event> getAll() {
        return events;
    }

    @Override
    public void addEvent(Event event) {
        event.setId(currentID.getAndIncrement());
        events.add(event);
    }


    @Override
    public void removeEvent(Event event) {
        events.remove(event);
    }

    @Override
    public void removeByTime(long time) {
        for (var x: events) {
            if (time == x.getTime()){
                events.remove(x);
            }
        }
    }

    @Override
    public List<Event> getEventsByTime(long time) {
        return events.stream().filter(x-> x.getTime() == time).collect(Collectors.toList());
    }
}
