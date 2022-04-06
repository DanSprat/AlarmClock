package ru.mishin.database.models;


import javax.persistence.*;

@Entity
@Table(name="EVENT")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private long time;
    @Column(name = "description")
    private String desc;

    public Event() {
    }

    public Event(int id, long time, String desc) {
        this.id = id;
        this.time = time;
        this.desc = desc;
    }


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

    public void setId(int id) {
        this.id = id;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
