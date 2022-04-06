package ru.mishin.database.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;


import org.hibernate.query.Query;
import ru.mishin.database.models.Event;
import ru.mishin.utils.HibernateSessionFactoryUtil;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class EventDao implements EventDAOImpl {
    @Override
    public List<Event> findAll() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Event> cr = cb.createQuery(Event.class);
        Root<Event> root = cr.from(Event.class);
        cr.select(root);
        Query<Event> query = session.createQuery(cr);
        List<Event> results = query.getResultList();
        return results;
    }

    public Event findEventById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Event.class,id);
    }

    @Override
    public void removeByTime(long time) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaDelete<Event> criteriaDelete = cb.createCriteriaDelete(Event.class);
        Root<Event> root = criteriaDelete.from(Event.class);
        criteriaDelete.where(cb.equal(root.get("time"), time));
        Transaction transaction = session.beginTransaction();
        session.createQuery(criteriaDelete).executeUpdate();
        transaction.commit();
    }

    public List<Event> findAllEqByTime(int time) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Event> cr = cb.createQuery(Event.class);
        Root<Event> root = cr.from(Event.class);
        cr.select(root).where(cb.equal(root.get("time"),time));
        Query<Event> query = session.createQuery(cr);
        List<Event> results = query.getResultList();
        return results;
    }

    public void save(Event event) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.persist(event);
        tx1.commit();
        session.close();
    }

    public void delete(Event event) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.remove(event);
        tx1.commit();
        session.close();
    }
}
