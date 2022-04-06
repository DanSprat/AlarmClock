package ru.mishin.utils;

import ru.mishin.database.models.Event;
import java.util.List;

public class SubscribeInfo {
    private long time;
    private List<ru.mishin.database.models.Event> events;

    public SubscribeInfo(long time ,final List<ru.mishin.database.models.Event> events) {
        this.events = events;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public List<Event> getEvents() {
        return events;
    }
}
