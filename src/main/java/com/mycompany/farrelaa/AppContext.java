package com.mycompany.farrelaa;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/** Pemegang Spring ApplicationContext (membaca applicationContext.xml sekali saja). */
public class AppContext {

    private static ApplicationContext context;

    public static synchronized ApplicationContext get() {
        if (context == null) {
            context = new ClassPathXmlApplicationContext("applicationContext.xml");
        }
        return context;
    }

    // Ambil bean (DAO) berdasarkan tipenya
    public static <T> T getBean(Class<T> tipe) {
        return get().getBean(tipe);
    }
}
