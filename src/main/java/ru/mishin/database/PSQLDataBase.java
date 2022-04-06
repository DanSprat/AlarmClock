package ru.mishin.database;

import ru.mishin.database.dao.EventDao;
import ru.mishin.database.models.Event;

import java.util.List;

public class PSQLDataBase implements IDataBase {
    EventDao eventDao = new EventDao();

    @Override
    public List<Event> getAll() {
        return eventDao.findAll();
    }

    @Override
    public void addEvent(Event event) {
        eventDao.save(event);
    }

    @Override
    public void removeEvent(Event event) {
        eventDao.delete(event);
    }

    @Override
    public void removeByTime(long time) {
        eventDao.removeByTime(time);
    }

    @Override
    public List<Event> getEventsByTime(long time) {
        return eventDao.findAllEqByTime((int)time);
    }
}
