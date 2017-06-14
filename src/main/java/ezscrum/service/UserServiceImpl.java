package ezscrum.service;

import ezscrum.model.User;
import ezscrum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private String username;

    //public UserServiceImpl (String username){/*this.username = username;*/}

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
        User us = new User();
        us.setUsername(user.getUsername());
        us.setEmail(user.getEmail());
        us.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        us.setEnabled(user.isEnabled());
        us.setSystemRole(user.getSystemRole());

        return userRepository.save(us);
    }

    @Override
    public User findUserById(Long id){
        return userRepository.findOne(id);
    }
}
