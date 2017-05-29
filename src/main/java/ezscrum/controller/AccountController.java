package ezscrum.controller;

import ezscrum.model.User;
import ezscrum.repositories.UserRepository;
import ezscrum.service.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "accounts")
public class AccountController {
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

//    @CrossOrigin(origins = "http://localhost:8080")
    @RequestMapping(method = RequestMethod.GET, path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

//    @CrossOrigin(origins = "http://localhost:8080")
//    @RequestMapping(method = RequestMethod.POST, path = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<User> getUser(@RequestParam String username, @RequestParam String password) {
//        User user = userService.findUserByUsername(username);
//        if (user != null){
//            System.out.println(user.getUsername());
//            System.out.println(user.getEmail());
//            System.out.println(user.getPassword());
//            System.out.println(user.isEnabled());
//        }
//        System.out.println(username);
//        System.out.println(password);
//        return new ResponseEntity<>(user, HttpStatus.FOUND);
//    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<JSONObject> getUserById(@PathVariable Long id) throws JSONException {
        User user = userService.findUserById(id);
        JSONObject account = new JSONObject();

        if (user != null){
            account.put("id", user.getId());
            account.put("username", user.getUsername());
            account.put("email", user.getEmail());
            account.put("enabled", user.isEnabled());
        }

        System.out.println(account);

        return new ResponseEntity<>(account, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/delete/{id}")
    public void delete(@PathVariable Long id){
        userService.delete(id);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody String addNewUser (@RequestBody User user) {
        userService.save(user);
        return "Saved";
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public @ResponseBody String updateUser (@PathVariable Long id, @RequestBody User user) {
        User update = userService.findUserById(id);
        update.setUsername(user.getUsername());
        update.setEmail(user.getEmail());
        update.setPassword(user.getPassword());
        userService.save(update);
        return "Updated";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody User user){
        userService.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
