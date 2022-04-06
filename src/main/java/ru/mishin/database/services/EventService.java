package ru.mishin.database.services;

import ru.mishin.database.dao.EventDao;
import ru.mishin.database.models.Event;

public class EventService {
    private EventDao eventDAO = new EventDao();

    public Event findEvent(int id) {
        return eventDAO.findEventById(id);
    }



}
