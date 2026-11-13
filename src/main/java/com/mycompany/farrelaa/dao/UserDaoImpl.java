package com.mycompany.farrelaa.dao;

import com.mycompany.farrelaa.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/** Implementasi UserDao memakai Hibernate. SessionFactory diisi Spring lewat setter. */
public class UserDaoImpl implements UserDao {

    private SessionFactory sessionFactory;

    // Getter dan setter (Spring memanggil setter ini: setter injection)
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User login(String username, String password) {
        try (Session s = sessionFactory.openSession()) {
            return s.createQuery("from User where username = :u and password = :p", User.class)
                    .setParameter("u", username).setParameter("p", password)
                    .setMaxResults(1).uniqueResult();
        }
    }
}
