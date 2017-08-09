package ezscrum.controller;

import ezscrum.Notification.Notification;
import ezscrum.model.User;
import ezscrum.repositories.UserRepository;
import ezscrum.service.UserService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(path = "accounts")
public class AccountController {
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping(method = RequestMethod.GET, path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String getAllUsers()  throws JSONException {
        Iterable<User> users = userRepository.findAll();
        JSONArray usersJSON = new JSONArray();
        for(User user : users){
            JSONObject userJSON = new JSONObject();
            userJSON.put("id", user.getId());
            userJSON.put("username", user.getUsername());
            userJSON.put("email", user.getEmail());
            userJSON.put("enabled", user.isEnabled());
            userJSON.put("systemrole", user.getSystemRole());
            userJSON.put("password", user.getPassword());
            userJSON.put("nickname", user.getNickname());
            usersJSON.put(userJSON);
        }
        JSONObject accounts = new JSONObject();
        accounts.put("accounts", usersJSON);
        return accounts.toString();
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET, produces =  MediaType.APPLICATION_JSON_VALUE)
    public  @ResponseBody String  validateUsername(@RequestParam(value = "username") String username){
        User user = userService.findUserByUsername(username);
        if(user != null)
            return "true";
       return "false";
    }

    @RequestMapping(value = "/getAccount",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    public @ResponseBody String getAccount(@RequestParam Map<String,String> requestParams) throws JSONException{
        String username = requestParams.get("username");
        String password = bCryptPasswordEncoder.encode(requestParams.get("password"));
        User user = userService.findUserByUsername(username);
        JSONObject account = new JSONObject();

        if(user != null /*&& password == user.getPassword()*/) {
            account.put("id", user.getId());
            account.put("username", user.getUsername());
            account.put("email", user.getEmail());
            account.put("enabled", user.isEnabled());
            account.put("systemrole", user.getSystemRole());
            account.put("password", user.getPassword());
            account.put("nickname", user.getNickname());
        }
        return account.toString();
    }

    @RequestMapping(value = "/getUserById/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String getUserById (@PathVariable ("id") String id) throws JSONException {
        User user = userService.findUserById(Long.valueOf(id));
        JSONObject account = new JSONObject();

        if (user != null){
            account.put("id", user.getId());
            account.put("username", user.getUsername());
            account.put("nickname", user.getNickname());
            account.put("email", user.getEmail());
            account.put("enabled", user.isEnabled());
            account.put("systemrole", user.getSystemRole());
        }

        return account.toString();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/delete/{id}")
    public boolean delete(@PathVariable Long id){
        userService.delete(id);
        return true;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody String addNewUser (@RequestBody Map<String, String>  payload) {
        User user;
        user =  userService.findUserByUsername(payload.get("username").toString());
        if(user != null)
            return "username exist";
        user = new User();
        user.setUsername(payload.get("username").toString());
        user.setPassword(payload.get("password").toString());
        user.setEmail(payload.get("email").toString());
        user.setNickname(payload.get("nickname").toString());
        user.setEnabled(Boolean.valueOf(payload.get("enabled")));
        user.setSystemRole(Boolean.valueOf(payload.get("systemrole")));
        userService.save(user);

        User response =  userService.findUserByUsername(payload.get("username").toString());
        return response.getId().toString();
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public @ResponseBody String updateUser (@PathVariable Long id, @RequestBody Map<String, String>  payload)  throws JSONException{
        User update = userService.findUserByUsername(payload.get("username"));
        if(update == null){
            return "send incorrect data";
        }
        long updateId = update.getId();
        long pathId = Long.valueOf(id);
        if(updateId != pathId) {
            return "username is not correct";}

        update.setEmail(payload.get("email").toString());
        update.setNickname(payload.get("nickname").toString());
        update.setEnabled(Boolean.valueOf(payload.get("enabled")));
        if(payload.get("password").toString() != null && !payload.get("password").toString().isEmpty() && !payload.get("password").toString().equals(""))
            update.setPassword(bCryptPasswordEncoder.encode(payload.get("password").toString()));

        userRepository.save(update);
        JSONObject account = new JSONObject();
        account.put("id", update.getId());
        account.put("username", update.getUsername());
        account.put("nickname", update.getNickname());
        account.put("email", update.getEmail());
        account.put("enabled", update.isEnabled());
        account.put("systemrole", update.getSystemRole());

        return account.toString();
    }

    @RequestMapping(value = "/updateSystemRole/{id}", method = RequestMethod.PUT)
    public @ResponseBody String updateSystemRole (@PathVariable Long id, @RequestBody Map<String, String>  payload)  throws JSONException{
        User user = userService.findUserById(Long.valueOf(id));
        if(user == null){
            return "send incorrect data";}
        JSONObject account = new JSONObject();
        user.setSystemRole(Boolean.valueOf(payload.get("systemrole")));
        userRepository.save(user);
        if (user != null){
            account.put("id", user.getId());
            account.put("username", user.getUsername());
            account.put("nickname", user.getNickname());
            account.put("email", user.getEmail());
            account.put("enabled", user.isEnabled());
            account.put("systemrole", user.getSystemRole());}
        return account.toString();
    }

    @RequestMapping(value = "/checkConnect", method = RequestMethod.GET)
    public @ResponseBody boolean checkConnect(){
        return true;
    }

    @RequestMapping(value = "/getAccountList", method = RequestMethod.POST)
    public @ResponseBody String getAccountList (@RequestBody Map<String, Long[]>  payload) throws JSONException{
        Long[] ids = payload.get("accounts_id");
        JSONObject json = new JSONObject();
        for (long id:ids) {
            User user = userService.findUserById(Long.valueOf(id));
            if(user != null){
                json.put(String.valueOf(id),user.getUsername());
            }
        }
        return json.toString();
    }



    @RequestMapping(value = "/getNotificationSubscriptStatus", method = RequestMethod.POST)
    public @ResponseBody String getNotificationStatus(@RequestBody Map<String,String>  payload)throws JSONException{
        String userId = payload.get("account_id");
        String firebaseToken = payload.get("firebaseToken");

        User user = userService.findUserById(Long.valueOf(userId));
        if(user != null){
            try {
                Notification notification = new Notification();
                return notification.GetSubscriptionStatus(user.getUsername(),firebaseToken);
            }catch(IOException e){
                return "Connection Error";
            }
        }
        else
            return "This user is not exist.";
    }

    @RequestMapping(value = "/subscribeNotification", method = RequestMethod.POST)
    public @ResponseBody String subscribeNotification(@RequestBody Map<String,String>  payload)throws JSONException {
        String userId = payload.get("account_id");
        String firebaseToken = payload.get("firebaseToken");
        User user = userService.findUserById(Long.valueOf(userId));
        if(user != null){
            try {
                Notification notification = new Notification();
                return notification.Subscribe(user.getUsername(),firebaseToken);
            }catch(IOException e){
                return "Connection Error";
            }
        }
        else
            return "This user is not exist.";
    }

    @RequestMapping(value = "/cancelSubscribeNotification", method = RequestMethod.POST)
    public @ResponseBody String unSubscribeNotification(@RequestBody Map<String,String>  payload)throws JSONException {
        String userId = payload.get("account_id");
        String firebaseToken = payload.get("firebaseToken");
        User user = userService.findUserById(Long.valueOf(userId));
        if(user != null){
            try {
                Notification notification = new Notification();
                return notification.CancelSubscribe(user.getUsername(),firebaseToken);
            }catch(IOException e){
                return "Connection Error";
            }
        }
        return "This user is not exist.";
    }

    @RequestMapping(value = "/sendNotification", method = RequestMethod.POST)
    public @ResponseBody String sendNotification(@RequestBody Map<String,String>  payload)throws JSONException {
        JSONArray recipientsId = new JSONArray(payload.get("accounts_id")) ;
        String title = payload.get("title");
        String body = payload.get("body");
        String eventSource = payload.get("eventSource");
        String messageFilter = payload.get("filter");

        ArrayList<String> recipients = new ArrayList<String>();
        for (int index =0; index<recipientsId.length();index++){
            User user = userService.findUserById(Long.valueOf(recipientsId.getLong(index)));
            if(user != null){
                recipients.add(user.getUsername());
            }
        }

        String response = "";
        if(recipients.size() == 0){
            response = "Didn't have recipient.";
            return response;
        }

        try{
            Notification notification = new Notification();
            response = notification.SendMessage(title,body,eventSource,recipients,messageFilter);
        }catch (IOException e){
            response = "Connection Error";
        }

        return response;
    }

    @RequestMapping(value = "/notifyServiceLogout", method = RequestMethod.POST)
    public @ResponseBody String notifyServiceLogout(@RequestBody Map<String,String>  payload)throws JSONException {
        String userId = payload.get("account_id");
        String firebaseToken = payload.get("firebaseToken");
        User user = userService.findUserById(Long.valueOf(userId));
        if(user != null){
            try {
                Notification notification = new Notification();
                return notification.NotifyLogout(user.getUsername(),firebaseToken);
            }catch(IOException e){
                return "Connection Error";
            }
        }
        return "This user is not exist.";
    }

    @RequestMapping(value = "/updateProjectSubscriptStatus", method = RequestMethod.POST)
    public @ResponseBody String updateProjectSubscriptStatus(@RequestBody Map<String,String>  payload)throws JSONException{
        String userId = payload.get("account_id");
        String projectsStatus = payload.get("projectsStatus");
        User user = userService.findUserById(Long.valueOf(userId));
        if(user != null){
            try {
                Notification notification = new Notification();
                return notification.updateProjectScriptStatus(user.getUsername(), projectsStatus);
            }catch(IOException e){
                return "Connection Error";
            }
        }
        return "This user is not exist.";
    }
}
