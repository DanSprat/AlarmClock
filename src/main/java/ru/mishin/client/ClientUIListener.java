package ru.mishin.client;

import ru.mishin.database.models.Event;

import java.util.List;

public interface ClientUIListener {
    public void onAddEvent(Event event);
    public void onTime(long time);
    public void onAlarm(List<Event> events);
    public void onSubscribe(long time, List<Event> events);
    public void onReset();
}
