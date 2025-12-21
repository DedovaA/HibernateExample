import models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.UserService;
import utils.HibernateSessionFactoryUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private final UserService userService;
    private final Scanner scanner;

    public App() {
        this.userService = new UserService();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        LOGGER.info("Старт приложения HibernateExample...");

        App app = new App();

        try{
            app.showMainMenu();
        } catch (Exception e) {
            LOGGER.error("Ошибка: " + e.getMessage());
        } finally {
            HibernateSessionFactoryUtil.shutdownSession();
            LOGGER.info("Приложение HibernateExample завершено.");
        }
    }

    private void showMainMenu() {
        while (true) {
            System.out.println("\n====================Главное меню===================");
            System.out.println("1 -  Найти пользователя по ID.");
            System.out.println("2 -  Найти пользователя по email.");
            System.out.println("3 -  Создать пользователя.");
            System.out.println("4 -  Обновить пользователя.");
            System.out.println("5 -  Удалить пользователя.");
            System.out.println("6 -  Показать всех пользователей.");
            System.out.println("7 -  Показать общее количество пользователей.");
            System.out.println("0 -  Выход.");
            System.out.println("=====================================================");

            int userInput = Integer.parseInt(scanner.nextLine());

            switch (userInput) {
                case 1 -> findUserById();
                case 2 -> findUserByEmail();
                case 3 -> createUser();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 6 -> showAllUsers();
                case 7 -> getUsersCount();
                case 0 -> {
                    System.out.println("Завершение работы приложения.");
                    return;
                }
                default -> System.out.println("Неверный ввод, выберите действие из списка.");
            }
        }
    }

    private void findUserById() {
        System.out.println("==============Поиск пользователя по ID===============");

        System.out.println("Введите ID пользователя: ");
        Long id =  Long.parseLong(scanner.nextLine());
        User user;

        try {
            Optional<User> userOptional = userService.getUserById(id);
            if (!userOptional.isPresent()) {
                System.out.println("Пользователь с " + id + " не найден.");
                return;
            }
            user = userOptional.get();
            System.out.println("Найден пользователь " + user);
        } catch (Exception e) {
            System.out.println("Ошибка поиска пользователя.\n" + e.getMessage());
        }
    }

    private void findUserByEmail() {
        System.out.println("==============Поиск пользователя по email===============");

        System.out.println("Введите email пользователя:");
        String email = scanner.nextLine().trim();
        User user;

        try {
            Optional<User> userOptional = userService.getUserByEmail(email);
            if (!userOptional.isPresent()) {
                System.out.println("Пользователь с " + email + " не найден.");
                return;
            }
            user = userOptional.get();
            System.out.println("Найден пользователь " + user);
        } catch (Exception e) {
            System.out.println("Ошибка поиска пользователя.\n" + e.getMessage());
        }
    }

    private void createUser() {
        System.out.println("==============Создание нового пользователя===============");

        System.out.println("Введите имя: ");
        String name = scanner.nextLine().trim();
        System.out.println("Введите email: ");
        String email = scanner.nextLine().trim();
        System.out.println("Введите возраст: ");
        int age = Integer.parseInt(scanner.nextLine());

        try {
            User user = new User(name, email, age);
            userService.saveUser(user);
            System.out.println("Пользователь успешно создан." + user);
        } catch (Exception e) {
            System.out.println("Ошибка создания пользователя.\n" + e.getMessage());
        }
    }

    private void updateUser() {
        System.out.println("==============Обновление пользователя===============");

        System.out.println("Введите ID пользователя для обновления: ");
        Long id =  Long.parseLong(scanner.nextLine());

        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isEmpty()) {
            System.out.println("Пользователь с " + id + " не найден.");
            return;
        }
        User user = userOptional.get();

        System.out.println("Введите новые данные (оставьте пустым, чтобы сохранит текущее значение): ");

        System.out.println("Введите новое имя: ");
        String newName = scanner.nextLine().trim();
        if(!newName.isEmpty())
            user.setName(newName);

        System.out.println("Введите новый email: ");
        String newEmail = scanner.nextLine().trim();
        if(!newEmail.isEmpty())
            user.setEmail(newEmail);

        System.out.println("Введите новый возраст: ");
        String input = scanner.nextLine().trim();
        int newAge;
        if (!input.isEmpty()) {
            newAge = Integer.parseInt(scanner.nextLine());
            user.setAge(newAge);
        }

        try {
            userService.updateUser(user);
            System.out.println("данный пользователя успешно обновлены.");
        } catch (Exception e) {
            System.out.println("Ошибка обновления пользователя.\n" + e.getMessage());
        }
    }

    private void deleteUser() {
        System.out.println("==============Удаление пользователя===============");

        System.out.println("Введите ID пользователя для удаления: ");
        Long id =  Long.parseLong(scanner.nextLine());
        Optional<User> userOptional = userService.getUserById(id);
        if (!userOptional.isPresent()) {
            System.out.println("Пользователь с " + id + " не найден.");
            return;
        }
        User user = userOptional.get();
        try {
            userService.deleteUser(id);
            System.out.println("Пользователь " + user + " успешно удален.");
        } catch (Exception e) {
            System.out.println("Ошибка удаления пользователя.\n" + e.getMessage());
        }
    }

    private void showAllUsers() {
        System.out.println("==============Список всех пользователей===============");

        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("В базе данных нет пользователей.");
            } else {
                for (User user: users) {
                    System.out.println(user);
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка вывода списка всех пользователей.\n" + e.getMessage());
        }
    }

    private void getUsersCount() {
        System.out.println("==============Количество пользователей===============");

        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("В базе данных 0 пользователей.");
            } else {
                System.out.println("В базе данных " + users.size() + " пользователей.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка вывода общего количества пользователей.\n" + e.getMessage());
        }
    }

}
