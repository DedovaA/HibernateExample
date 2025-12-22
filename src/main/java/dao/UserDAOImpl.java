package dao;

import models.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.query.SelectionQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HibernateSessionFactoryUtil;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDAOImpl.class);

    @Override
    public Optional<User> findById(Long id) {
        User user = null;
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            user = session.find(User.class, id);
        } catch (Exception e) {
            LOGGER.error("Ошибка поиска пользователя по ID: {} .\n", id, e);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User user = null;
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            user = query.uniqueResult();
        } catch (Exception e) {
            LOGGER.error("Ошибка поиска пользователя c email: {} не найден .\n", email, e);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        List<User> users = null;
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            users = session.createQuery("FROM User", User.class).getResultList();
        } catch (Exception e) {
            LOGGER.error("Ошибка.\n", e);
        }
        return users;
    }

    @Override
    public void save(User user) {
        Transaction transaction = null;
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            LOGGER.info("Пользователь с email: {} успешно сохранен.\n", user.getEmail());
        } catch (Exception e) {
            if(transaction !=null) {
                transaction.rollback();
            }
            LOGGER.error("Ошибка сохранения пользователя.\n", e);
        }
    }

    @Override
    public void update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            LOGGER.info("Пользователь с ID: {} успешно обновлен. \n", user.getId());
        } catch (Exception e) {
            if(transaction !=null) {
                transaction.rollback();
            }
            LOGGER.error("Ошибка обновления пользователя с ID: {}.\n", user.getId(), e);
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.find(User.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                LOGGER.info("Пользователь с ID: {} успешно удален. \n", id);
            } else {
                LOGGER.warn("Пользователь с ID: {} не найден. \n", id);
                transaction.rollback();
            }
        } catch (Exception e) {
            if(transaction !=null) {
                transaction.rollback();
            }
            LOGGER.error("Ошибка удаления пользователя с ID: {}.\n", id, e);
        }
    }

    @Override
    public Long count() {
        Long count = null;
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            SelectionQuery<Long> query = session.createSelectionQuery("SELECT COUNT(u) FROM User u", Long.class);
            count = query.getSingleResult();
        } catch (Exception e) {
            LOGGER.error("Ошибка получения кол-ва пользователей.\n",  e);
        }
        return count;
    }
}
