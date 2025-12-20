package utils;

import models.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
public class HibernateSessionFactoryUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateSessionFactoryUtil.class);
    private static SessionFactory sessionFactory;

    static {
        try {
            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml")
                    .build();
            Metadata metadata = new MetadataSources(serviceRegistry)
                    .addAnnotatedClass(User.class)
                    .getMetadataBuilder()
                    .build();
            sessionFactory = metadata.getSessionFactoryBuilder().build();
            LOGGER.info("Hibernate SessionFactory успешно создан.\n");
        } catch (Exception e) {
            LOGGER.error("Ошибка создания Hibernate SessionFactory\n", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            LOGGER.info("Hibernate SessionFactory закрыта.\n");
        }
    }
}
 */


public class HibernateSessionFactoryUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateSessionFactoryUtil.class);
    private static SessionFactory sessionFactory;

    public HibernateSessionFactoryUtil() {}

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                configuration.addAnnotatedClass(User.class);
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());
                LOGGER.info("Hibernate SessionFactory успешно создана.\n");
            } catch (Exception e) {
                LOGGER.error("Ошибка создания Hibernate SessionFactory\n", e);
            }
        }
        return sessionFactory;
    }

    public static void shutdownSession() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            LOGGER.info("Hibernate SessionFactory закрыта.\n");
        }
    }
}
