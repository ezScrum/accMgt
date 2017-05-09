package ezscrum.controller;

import ezscrum.model.User;
import ezscrum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller    // This means that this class is a Controller
@RequestMapping(path="/demo") // This means URL's start with /demo (after Application path)
public class MainController {
//	@RequestMapping(method = RequestMethod.GET, path = "/all")
//	public @ResponseBody Iterable<User> getAllUsers() {
//		return userRepository.findAll();
//	}
//
//	@RequestMapping(value = "/add", method = RequestMethod.POST)
//	public @ResponseBody
//	String addNewUser (@RequestParam String name, @RequestParam String email, @RequestParam String password) {
//		User n = new User();
//		n.setUserame(name);
//		n.setPassword(password);
//		n.setEmail(email);
//		userRepository.save(n);
//		return "Saved";
//	}
}
