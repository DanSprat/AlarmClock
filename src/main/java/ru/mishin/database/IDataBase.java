package ru.mishin.database;

import ru.mishin.database.models.Event;

import java.util.List;

public interface IDataBase {
    public List<Event> getAll();
    public void addEvent(Event event);
    public void removeEvent(Event event);
    public void removeByTime(long time);
    public List<Event> getEventsByTime(long time);
}
