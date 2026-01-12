package dao;

import models.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import utils.HibernateSessionFactoryUtil;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
class UserDAOImplTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;
    private UserDAOImpl userDAO;

    @BeforeAll
    static void beforeAll() {
        Configuration configuration = new Configuration();
        Properties settings = new Properties();
        settings.put(Environment.JAKARTA_JDBC_DRIVER, "org.postgresql.Driver");
        settings.put(Environment.JAKARTA_JDBC_URL, postgres.getJdbcUrl());
        settings.put(Environment.JAKARTA_JDBC_USER, postgres.getUsername());
        settings.put(Environment.JAKARTA_JDBC_PASSWORD, postgres.getPassword());
        settings.put(Environment.HBM2DDL_AUTO, "create-drop");

        configuration.setProperties(settings);
        configuration.addAnnotatedClass(User.class);

        sessionFactory = configuration.buildSessionFactory();

    }

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOImpl(sessionFactory);

        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @AfterAll
    static void tearDownDatabase() {
        HibernateSessionFactoryUtil.shutdownSession();
    }

    @AfterEach
    void tearDown() {}

    @Test
    @DisplayName("Должен возвращать пользователя по id из БД.")
    void findById_Success() {
        User ivan = new User("Ivan", "ivan@mail.ru", 27);
        userDAO.save(ivan);

        Optional<User> result = userDAO.findById(ivan.getId());

        assertTrue(result.isPresent());
        assertEquals("Ivan", result.get().getName());
        assertEquals("ivan@mail.ru", result.get().getEmail());
        assertEquals(27, result.get().getAge());
    }

    @Test
    @DisplayName("Должен возвращать empty по несуществующему id.")
    void findById_UserNotExist() {
        Optional<User> result = userDAO.findById(100L);
        assertDoesNotThrow(() -> userDAO.findById(1000L));

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Должен возвращать пользователя по email из БД.")
    void findByEmail_Success() {
        User ivan = new User("Ivan", "ivan@mail.ru", 27);

        userDAO.save(ivan);

        Optional<User> result = userDAO.findByEmail("ivan@mail.ru");

        assertTrue(result.isPresent());
        assertEquals("Ivan", result.get().getName());
        assertEquals("ivan@mail.ru", result.get().getEmail());
        assertEquals(27, result.get().getAge());
    }

    @Test
    @DisplayName("Должен возвращать empty при запросе пользователя по несуществующему email.")
    void findByEmail_UserNotExist() {
        User ivan = new User("Ivan", "ivan@mail.ru", 27);

        userDAO.save(ivan);

        Optional<User> result = userDAO.findByEmail("notexisted@mail.ru");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Должен возвращать список всех пользователей из БД.")
    void findAll_Success() {
        User ivan = new User("Ivan", "ivan@mail.ru", 27);
        User oleg = new User("Oleg", "oleg@mail.ru",28);

        userDAO.save(ivan);
        userDAO.save(oleg);

        List<User> result = userDAO.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("ivan@mail.ru")));
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("oleg@mail.ru")));
    }

    @Test
    @DisplayName("Должен возвращать пустой список пользователей из БД.")
    void findAll_ReturnEmpty() {
        List<User> result = userDAO.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Должен успешно сохранять пользователя в БД.")
    void save_Success() {
        User ivan = new User("Ivan", "ivan@mail.ru", 27);

        userDAO.save(ivan);

        Optional<User> result = userDAO.findById(ivan.getId());

        assertTrue(result.isPresent());
        assertEquals("Ivan", result.get().getName());
        assertEquals("ivan@mail.ru", result.get().getEmail());
        assertEquals(27, result.get().getAge());
    }

    @Test
    @DisplayName("Не должен сохранять пользователя с не уникальным email в БД.")
    void save_EmailAlreadyExist() {
        User ivan = new User("Ivan", "ivan@mail.ru", 27);
        User anton = new User("Anton", "ivan@mail.ru", 29);

        userDAO.save(ivan);

        assertThrows(IllegalStateException.class, () -> userDAO.save(anton));

        assertEquals(1L, userDAO.count());

        Optional<User> result1 = userDAO.findById(ivan.getId());
        assertTrue(result1.isPresent());
        Optional<User> result2 = userDAO.findById(anton.getId());
        assertFalse(result2.isPresent());
    }

    @Test
    @DisplayName("Не должен сохранять пользователя без имени в БД.")
    void save_FailedWithoutName() {
        User user = new User();
        user.setEmail("ivan@mail.ru");
        user.setAge(27);

        assertThrows(IllegalStateException.class, () -> userDAO.save(user));

        assertEquals(0L, userDAO.count());
    }

    @Test
    @DisplayName("Не должен сохранять пользователя без email в БД.")
    void save_FailedWithoutEmail() {
        User user = new User();
        user.setName("Ivan");
        user.setAge(27);

        assertThrows(IllegalStateException.class, () -> userDAO.save(user));

        assertEquals(0L, userDAO.count());
    }

    @Test
    @DisplayName("Должен успешно обновлять пользователя в БД.")
    void update_Success() {
        User ivan = new User("Ivan", "ivan@mail.ru", 27);

        userDAO.save(ivan);

        Optional<User> user = userDAO.findById(ivan.getId());

        assertTrue(user.isPresent());

        User existingUser = user.get();
        existingUser.setName("Vanya");
        userDAO.update(existingUser);

        Optional<User> updatedUser = userDAO.findById(ivan.getId());

        assertTrue(updatedUser.isPresent());
        assertEquals("Vanya", updatedUser.get().getName());
        assertEquals("ivan@mail.ru", updatedUser.get().getEmail());
        assertEquals(27, updatedUser.get().getAge());
    }

    @Test
    @DisplayName("Не должен обновлять пользователя, если дублируется email.")
    void update_FailedEmailNotUnique() {
        User ivan = new User("Ivan", "ivan@mail.ru", 27);
        User oleg = new User("Oleg", "oleg@mail.ru",28);
        userDAO.save(ivan);
        userDAO.save(oleg);
        oleg.setEmail("ivan@mail.ru");

        userDAO.update(oleg);

        Optional<User> userOleg = userDAO.findById(oleg.getId());

        assertTrue(userOleg.isPresent());
        assertEquals("Oleg", userOleg.get().getName());
        assertEquals("oleg@mail.ru", userOleg.get().getEmail());
        assertEquals(28, userOleg.get().getAge());
    }

    @Test
    @DisplayName("Не должен обновлять пользователя, если не задан email.")
    void update_FailedEmailIsEmpty() {
        User oleg = new User("Oleg", "oleg@mail.ru",28);
        userDAO.save(oleg);
        oleg.setEmail(null);

        userDAO.update(oleg);

        Optional<User> userOleg = userDAO.findById(oleg.getId());

        assertTrue(userOleg.isPresent());
        assertEquals("Oleg", userOleg.get().getName());
        assertEquals("oleg@mail.ru", userOleg.get().getEmail());
        assertEquals(28, userOleg.get().getAge());
    }

    @Test
    @DisplayName("Не должен обновлять пользователя, если не задано имя.")
    void update_FailedNameIsEmpty() {
        User oleg = new User("Oleg", "oleg@mail.ru",28);
        userDAO.save(oleg);

        oleg.setName(null);

        userDAO.update(oleg);

        Optional<User> userOleg = userDAO.findById(oleg.getId());

        assertTrue(userOleg.isPresent());
        assertEquals("Oleg", userOleg.get().getName());
        assertEquals("oleg@mail.ru", userOleg.get().getEmail());
        assertEquals(28, userOleg.get().getAge());
    }

    @Test
    @DisplayName("Должен успешно удалять пользователя из БД.")
    void delete_Success() {
        User ivan = new User("Ivan", "ivan@mail.ru", 27);
        User oleg = new User("Oleg", "oleg@mail.ru",28);
        userDAO.save(ivan);
        userDAO.save(oleg);

        assertEquals(2L, userDAO.count());

        userDAO.delete(ivan.getId());

        assertEquals(1L, userDAO.count());

        Optional<User> removedUser = userDAO.findById(ivan.getId());
        assertFalse(removedUser.isPresent());

        Optional<User> existedUser = userDAO.findById(oleg.getId());
        assertTrue(existedUser.isPresent());
    }

    @Test
    @DisplayName("Не должен удалять пользователя по несуществующему ID.")
    void delete_FailedIdNotFound() {
        User ivan = new User("Ivan", "ivan@mail.ru", 27);

        userDAO.save(ivan);

        assertEquals(1L, userDAO.count());

        userDAO.delete(3000L);
        assertDoesNotThrow(() -> userDAO.findById(1000L));

        assertEquals(1L, userDAO.count());
    }

    @Test
    @DisplayName("Должен успешно возвращать количество всех пользователей из БД.")
    void count_Success() {
        User ivan = new User("Ivan", "ivan@mail.ru", 27);
        User oleg = new User("Oleg", "oleg@mail.ru",28);
        userDAO.save(ivan);
        assertEquals(1L, userDAO.count());

        userDAO.save(oleg);
        assertEquals(2L, userDAO.count());

        userDAO.delete(ivan.getId());
        userDAO.delete(oleg.getId());
        assertEquals(0L, userDAO.count());
    }
}