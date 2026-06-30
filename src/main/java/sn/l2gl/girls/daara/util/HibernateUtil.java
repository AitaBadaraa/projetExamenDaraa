package sn.l2gl.girls.daara.util;

import sn.l2gl.girls.daara.exception.HibernateConfigException;
import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
//import sn.youdev.hib.exception.HibernateConfigException;

public class HibernateUtil {
    private HibernateUtil() {
        /* This utility class should not be instantiated */
    }


    @Getter
    private static final SessionFactory sessionFactory = build();

    private static SessionFactory build() {
        try {
            // Load config (hibernate.cfg.xml)
            StandardServiceRegistry registry =
                    new StandardServiceRegistryBuilder()
                            .configure("hibernate.cfg.xml")
                            .build();

            Metadata metadata = new MetadataSources(registry)
                    .getMetadataBuilder()
                    .build();

            return metadata.getSessionFactoryBuilder().build();

        } catch (Exception e) {
            throw new HibernateConfigException(e);
        }
    }

    public static void shutdown() {
        sessionFactory.close();
    }
}