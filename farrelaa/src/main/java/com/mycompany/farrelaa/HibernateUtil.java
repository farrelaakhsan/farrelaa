package com.mycompany.farrelaa;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/** Membuat satu SessionFactory Hibernate (dibaca dari hibernate.cfg.xml). */
public class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }
        return sessionFactory;
    }
}
