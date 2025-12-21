package dao;

import models.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void save(User user);
    void update(User user);
    void delete(Long id);
    Long count();
}
