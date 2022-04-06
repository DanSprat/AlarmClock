package ru.mishin.database.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.mishin.database.models.Event;
import ru.mishin.utils.HibernateSessionFactoryUtil;

import java.util.List;

public interface EventDAOImpl {
    public List<Event> findAll();
    public Event findEventById(int id);
    public void removeByTime(long time);
    public List<Event> findAllEqByTime(int time) ;
    public void save(Event event);
    public void delete(Event event);

}
