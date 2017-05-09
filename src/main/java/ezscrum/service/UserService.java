package ezscrum.service;

import ezscrum.model.User;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface UserService {
    User findUserByUsername(String username);

    List<User> getUserList(int page, int size, Sort.Direction sort);

    void delete(Long id);

    User save(User user);

    User findUserById(Long id);
}
