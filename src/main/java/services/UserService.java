package services;

import dao.UserDAO;
import dao.UserDAOImpl;
import models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAOImpl();
    }

    //конструктор для mock-тестов и DI
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Optional<User> getUserById(Long id) {
        Optional<User> user = Optional.empty();
        try {
            user = userDAO.findById(id);
            if (user.isPresent()) {
                LOGGER.info("Пользователь с id {} успешно получен.\n", id);
            } else {
                LOGGER.info("Пользователь с id {} не найден.\n", id);
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка получения пользователя с ID: {}.", id, e);
        }
        return user;
    }

    public Optional<User> getUserByEmail(String email) {
        Optional<User> user = Optional.empty();
        try {
            user = userDAO.findByEmail(email);
            if (user.isPresent()) {
                LOGGER.info("Пользователь с email {} успешно получен.\n", email);
            } else {
                LOGGER.info("Пользователь с email {} не найден.\n", email);
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка получения пользователя с email: {}.", email, e);
        }
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = null;
        try {
            users = userDAO.findAll();
            if (users != null) {
                LOGGER.info("Список всех пользователей успешно получен.\n");
            } else {
                LOGGER.info("Список пользователей пуст.\n");
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка получения списка пользователей.", e);
        }
        return users;
    }

    public void saveUser(User user) {
        try {
            userDAO.save(user);
            LOGGER.info("Пользователь {} успешно создан.\n", user);
        } catch (Exception e) {
            LOGGER.error("Ошибка создания пользователя.", e);
        }
    }

    public void updateUser(User user) {
        try {
            userDAO.update(user);
            LOGGER.info("Пользователь {} успешно обновлен.\n", user);
        } catch (Exception e) {
            LOGGER.error("Ошибка обновления пользователя.", e);
        }
    }

    public void deleteUser(Long id) {
        try {
            userDAO.delete(id);
            LOGGER.info("Пользователь с id {} успешно удален.\n", id);
        } catch (Exception e) {
            LOGGER.error("Ошибка удаления пользователя.", e);
        }
    }

    public Long getUsersCount() {
        Long usersCount = null;
        try {
            usersCount = userDAO.count();
            LOGGER.info("В базе данных {} пользователей.", usersCount);
        } catch (Exception e) {
            LOGGER.error("Ошибка получения общего количества пользователей.", e);
        }
        return usersCount;
    }
}
