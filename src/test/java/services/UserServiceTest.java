package services;

import dao.UserDAO;
import models.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    private User user;
    private  Long id;
    private String email;

    @BeforeEach
    void setUp() {
        user = new User("Ivan", "ivan@mail.ru", 27);
        id = 1L;
        user.setId(id);
        email = user.getEmail();
    }

    @AfterEach
    void tearDown() {
    }

    @DisplayName("Должен возвращать пользователя по id из БД.")
    @Test
    void getUserById_Success() {
        when(userDAO.findById(id)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(id);

        assertTrue(result.isPresent());
        assertEquals("Ivan", result.get().getName());
        verify(userDAO, times(1)).findById(id);
    }

    @DisplayName("Должен возвращать empty при запросе пользователя из БД.")
    @Test
    void getUserById_NotFound() {
        when(userDAO.findById(id)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(id);

        assertFalse(result.isPresent());
        verify(userDAO, times(1)).findById(id);
    }

    @DisplayName("Должен бросать исключение при попытке получения пользователя из БД.")
    @Test
    void getUserById_HandleException() {
        when(userDAO.findById(id)).thenThrow(new RuntimeException("Ошибка БД."));

        Optional<User> result = userService.getUserById(id);
        //вернет empty
        assertFalse(result.isPresent());
        verify(userDAO, times(1)).findById(id);
    }

    @DisplayName("Должен возвращать пользователя по email из БД.")
    @Test
    void getUserByEmail_Success() {
        when(userDAO.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByEmail(email);

        assertTrue(result.isPresent());
        assertEquals("ivan@mail.ru", result.get().getEmail());
        verify(userDAO, times(1)).findByEmail(email);
    }

    @DisplayName("Должен возвращать empty при запросе пользователя по email из БД.")
    @Test
    void getUserByEmail_NotFound() {
        when(userDAO.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByEmail(email);

        assertFalse(result.isPresent());
        verify(userDAO, times(1)).findByEmail(email);
    }

    @DisplayName("Должен бросать исключение при попытке получения пользователя по email из БД.")
    @Test
    void getUserByEmail_HandleException() {
        when(userDAO.findByEmail(email)).thenThrow(new RuntimeException("Ошибка БД."));

        Optional<User> result = userService.getUserByEmail(email);
        //вернет empty
        assertFalse(result.isPresent());
        verify(userDAO, times(1)).findByEmail(email);
    }


    @DisplayName("Должен возвращать список всех пользователей из БД.")
    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(
                new User("Ivan", "ivan@mail.ru",27),
                new User("Oleg", "oleg@mail.ru",28)
        );
        when(userDAO.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userDAO, times(1)).findAll();
    }



    @DisplayName("Должен успешно сохранять пользователя в БД.")
    @Test
    void testSaveUser_Success() {
        userService.saveUser(user);

        verify(userDAO, times(1)).save(user);
    }

    @DisplayName("Должен успешно обновлять пользователя в БД.")
    @Test
    void updateUser_Success() {
        userService.updateUser(user);

        verify(userDAO, times(1)).update(user);
    }

    @DisplayName("Должен бросать исключение при попытке обновления пользователя в БД.")
    @Test
    void updateUser_HandleException() {
        doThrow(new RuntimeException("Ошибка БД.")).when(userDAO).update(any(User.class));
        assertDoesNotThrow(() -> userService.updateUser(user));

        verify(userDAO, times(1)).update(user);
    }

    @DisplayName("Должен успешно удалять пользователя из БД.")
    @Test
    void deleteUser_Success() {
        userService.deleteUser(id);

        verify(userDAO, times(1)).delete(id);
    }

    @DisplayName("Должен успешно возвращать количество всех пользователей из БД.")
    @Test
    void getUsersCount_Success() {

        when(userDAO.count()).thenReturn(4L);

        Long result = userService.getUsersCount();

        assertEquals(4L, result);
        verify(userDAO, times(1)).count();
    }
}