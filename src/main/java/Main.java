import models.User;
import services.UserService;

import java.sql.SQLOutput;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService();


//        System.out.println("Количество пользователей в таблице:\n" + userService.getUsersCount());
//        System.out.println("Список пользователей\n" + userService.getAllUsers());

//        User Masha = new User("Masha", "masha@mail.ru", 23);
//        User Igor = new User("Igor", "igor@mail.ru", 26);
//        User Valera = new User("Valera", "valera@mail.ru", 27);
//        userService.deleteUser(3L);
//        userService.saveUser(Igor);
//        userService.saveUser(Valera);

//        System.out.println("Количество пользователей в таблице:\n" + userService.getUsersCount());
//        System.out.println("Список пользователей\n" + userService.getAllUsers());

//        System.out.println("Пользователь с id 1\n" + userService.getUserById(1L));
//        System.out.println("Пользователь с имейл\n" + userService.getUserByEmail("masha@mail.ru"));;

//        User userForUpdate = userService.getUserById(2L).orElse(null);
//        if (userForUpdate != null) {
//            userForUpdate.setAge(26);
//            userService.updateUser(userForUpdate);
//        } else {
//            System.out.println("Пользователь не найден");
//        }

    }
}
