package ru.mishin.server;

public class Event {
   private long time;
   private String desc;

    public Event(long time, String desc) {
        this.time = time;
        this.desc = desc;
    }

    public long getTime() {
        return time;
    }

    public String getDesc() {
        return desc;
    }
}
