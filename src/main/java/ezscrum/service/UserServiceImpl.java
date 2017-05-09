package ezscrum.service;

import ezscrum.model.User;
import ezscrum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User findUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getUserList(int page, int size, Sort.Direction sort){
        return userRepository.findAll(new PageRequest(page, size, sort, "username")).getContent();
    }

    @Override
    public void delete(Long id){
        userRepository.delete(id);
    }

    @Override
    public User save(User user){
        return userRepository.save(user);
    }

    @Override
    public User findUserById(Long id){
        return userRepository.findOne(id);
    }
}
