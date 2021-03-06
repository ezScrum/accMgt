package ezscrum.controller;

import com.jayway.restassured.response.Response;
import ezscrum.model.User;
import ezscrum.repositories.UserRepository;
import ezscrum.service.UserService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;


import static com.jayway.restassured.RestAssured.given;

import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anfa on 2017/7/7.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerTest {
    @LocalServerPort
    private int port;

    private String username = "admin";
    private String password = "admin";
    private String token;
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Before
    public void setUp(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","admin");
            json.put("password","admin");
        }catch (JSONException e){
        }
        Response response = given().port(port).contentType("application/json").body(json.toString())
                                .when().post("/login")
                                .then().extract().response();
        token = response.header("Authorization");


    }

    @After
    public void tearDown()  {

        userRepository.deleteAll();
        userRepository.flush();
        JSONObject json = new JSONObject();
        try{
            json.put("username","admin");
            json.put("password","admin");
            json.put("nickname","admin");
            json.put("email","Scrumteam@ezScrum.com.tw");
            json.put("enabled",true);
            json.put("systemrole",true);
        }catch (JSONException e){
        }
        String response = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        System.out.println("response = "+response);
    }
    private void addAccount(){

    }
    @Test
    public void LoginTest(){
        Assert.assertNotNull(token);
    }
    @Test
    public void LoginFailTest(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","invalid");
            json.put("password","invalid");
        }catch (JSONException e){
        }
        Response response = given().port(port).contentType("application/json").body(json.toString())
                .when().post("/login")
                .then().extract().response();
        String token2 = response.header("Authorization");
        Assert.assertNull(token2 );
    }

    @Test
    public void addExistAccountTest() {
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        }catch (JSONException e){
        }
        String response = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        long responseId = Long.valueOf(response);
        String response2 = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        System.out.println(response2);
        Assert.assertEquals("username exist", response2);

        userService.delete(responseId);
    }
    @Test
    public void addAccountTest() {
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        }catch (JSONException e){
        }
        String response = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        System.out.println("response = "+response);
        long ExpectedId = Long.valueOf(response);

        User user = userService.findUserByUsername("Acount01");
        long ActualId = user.getId();
        Assert.assertEquals(ExpectedId, ActualId);

        userService.delete(ExpectedId);
    }
    @Test
    public void updateIdIncorrectUsername(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
        }
        String response = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        long ExpectedId = Long.valueOf(response);
        JSONObject jsonEdit = new JSONObject();
        try{
            jsonEdit.put("username","Acount01Edit");
            jsonEdit.put("password","Acount01Edit");
            jsonEdit.put("nickname","Acount01Edit");
            jsonEdit.put("email","Acount01@gmail.com");
            jsonEdit.put("enabled",true);
            jsonEdit.put("systemrole",false);
        } catch (JSONException e){
        }
        String Actual = given().header("Authorization", token).port(port).contentType("application/json").body(jsonEdit.toString())
                .when().put("/accounts/update/" + ExpectedId).andReturn().asString();

        Assert.assertEquals("send incorrect data" ,Actual);


        userService.delete(ExpectedId);
    }
    @Test
    public void updateIdIncorrectId(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
        }
        String response = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        long ExpectedId = Long.valueOf(response) + 1 ;
        JSONObject jsonEdit = new JSONObject();
        try{
            jsonEdit.put("username","Acount01");
            jsonEdit.put("password","Acount01Edit");
            jsonEdit.put("nickname","Acount01Edit");
            jsonEdit.put("email","Acount01@gmail.com");
            jsonEdit.put("enabled",true);
            jsonEdit.put("systemrole",false);
        } catch (JSONException e){
        }
        String Actual = given().header("Authorization", token).port(port).contentType("application/json").body(jsonEdit.toString())
                .when().put("/accounts/update/" + ExpectedId).andReturn().asString();

        Assert.assertEquals("username is not correct" ,Actual);


        userService.delete(ExpectedId-1);
    }
    @Test
    public void updateAccountTest(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
        }
        String response = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        long ExpectedId = Long.valueOf(response);
        JSONObject jsonEdit = new JSONObject();
        try{
            jsonEdit.put("username","Acount01");
            jsonEdit.put("password","Acount01Edit");
            jsonEdit.put("nickname","Acount01Edit");
            jsonEdit.put("email","Acount01Edit@gmail.com");
            jsonEdit.put("enabled",true);
            jsonEdit.put("systemrole",false);
        } catch (JSONException e){
            e.printStackTrace();
        }
        String Actual = given().header("Authorization", token).port(port).contentType("application/json").pathParam("id",ExpectedId).body(jsonEdit.toString())
                .when().put("/accounts/update/{id}").andReturn().asString();
        try {
            JSONObject Actualjson = new JSONObject(Actual);
            Assert.assertEquals(jsonEdit.get("username").toString(), Actualjson.get("username").toString());
            Assert.assertEquals(jsonEdit.get("nickname").toString(), Actualjson.get("nickname").toString());
            Assert.assertEquals(jsonEdit.get("email").toString(), Actualjson.get("email").toString());
            Assert.assertEquals(jsonEdit.get("enabled").toString(), Actualjson.get("enabled").toString());
            Assert.assertEquals(jsonEdit.get("systemrole").toString(), Actualjson.get("systemrole").toString());
            } catch (JSONException e) {
            e.printStackTrace();
        }
        userService.delete(ExpectedId);
    }

    @Test
    public void updateSystemRole(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
        }
        String response = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        long ExpectedId = Long.valueOf(response);
        JSONObject jsonEdit = new JSONObject();
        try{
            jsonEdit.put("systemrole",true);
        } catch (JSONException e){
        }
        String Actual = given().header("Authorization", token).port(port).contentType("application/json").pathParam("id", ExpectedId).body(jsonEdit.toString())
                .when().put("/accounts/updateSystemRole/{id}").andReturn().asString();

        try {
            JSONObject Actualjson = new JSONObject(Actual);
            Assert.assertEquals(json.get("username").toString(), Actualjson.get("username").toString());
            Assert.assertEquals(json.get("nickname").toString(), Actualjson.get("nickname").toString());
            Assert.assertEquals(json.get("email").toString(), Actualjson.get("email").toString());
            Assert.assertEquals(json.get("enabled").toString(), Actualjson.get("enabled").toString());
            Assert.assertEquals(jsonEdit.get("systemrole").toString(), Actualjson.get("systemrole").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        userService.delete(ExpectedId);
    }

    @Test
    public void updateSystemRoleIncorrectId(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
        }
        String response = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        long ExpectedId = Long.valueOf(response)+1;
        JSONObject jsonEdit = new JSONObject();
        try{
            jsonEdit.put("systemrole",true);
        } catch (JSONException e){
        }
        String Actual = given().header("Authorization", token).port(port).contentType("application/json").body(jsonEdit.toString())
                .when().put("/accounts/updateSystemRole/" + ExpectedId).andReturn().asString();

        Assert.assertEquals("send incorrect data" ,Actual);
        userService.delete(ExpectedId-1);
    }

    @Test
    public void checkConnect(){
        String Actual = given().header("Authorization", token).port(port).contentType("application/json")
                .when().get("/accounts/checkConnect").andReturn().asString();
        System.out.println(Actual);
        boolean check = Boolean.valueOf(Actual);
        Assert.assertTrue(check);
    }
    @Test
    public void checkUsername(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
        }
        String response = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        long ExpectedId = Long.valueOf(response);
        JSONObject jsonCheck = new JSONObject();
        try{
            jsonCheck.put("username","Acount01");
        } catch (JSONException e){
        }
        String Actual = given().header("Authorization", token).port(port).contentType("application/json").param("username", "Acount01")
                .when().get("/accounts/check").andReturn().asString();
        Assert.assertEquals("true" ,Actual);

        String ActualFals = given().header("Authorization", token).port(port).contentType("application/json").param("username", "Illegal")
                .when().get("/accounts/check").andReturn().asString();
        Assert.assertEquals("false" ,ActualFals);
        userService.delete(ExpectedId);
    }

    @Test
    public void getAccount(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
        }
        String response = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        long ExpectedId = Long.valueOf(response);

        String Actual = given().header("Authorization", token).port(port).contentType("application/json").param("username", "Acount01").param("password","Acount01")
                .when().get("/accounts/getAccount").andReturn().asString();

        try {
            JSONObject Actualjson = new JSONObject(Actual);
            Assert.assertEquals(response, Actualjson.get("id").toString());
            Assert.assertEquals(response, Actualjson.get("id").toString());
            Assert.assertEquals(json.get("username").toString(), Actualjson.get("username").toString());
            Assert.assertEquals(json.get("nickname").toString(), Actualjson.get("nickname").toString());
            Assert.assertEquals(json.get("email").toString(), Actualjson.get("email").toString());
            Assert.assertEquals(json.get("enabled").toString(), Actualjson.get("enabled").toString());
            Assert.assertEquals(json.get("systemrole").toString(), Actualjson.get("systemrole").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        userService.delete(ExpectedId);
    }

    @Test
    public void getUserById(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
        }
        String response = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        long ExpectedId = Long.valueOf(response);

        String Actual = given().header("Authorization", token).port(port).contentType("application/json").pathParam("id",response)
                .when().get("/accounts/getUserById/{id}").andReturn().asString();

        try {
            JSONObject Actualjson = new JSONObject(Actual);
            Assert.assertEquals(response, Actualjson.get("id").toString());
            Assert.assertEquals(json.get("username").toString(), Actualjson.get("username").toString());
            Assert.assertEquals(json.get("nickname").toString(), Actualjson.get("nickname").toString());
            Assert.assertEquals(json.get("email").toString(), Actualjson.get("email").toString());
            Assert.assertEquals(json.get("enabled").toString(), Actualjson.get("enabled").toString());
            Assert.assertEquals(json.get("systemrole").toString(), Actualjson.get("systemrole").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        userService.delete(ExpectedId);
    }

    @Test
    public void deleteAccount(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
        }
        String response = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();

        String Actual = given().header("Authorization", token).port(port).contentType("application/json").pathParam("id",response)
                .when().post("/accounts/delete/{id}").andReturn().asString();
        boolean check = Boolean.valueOf(Actual);
        Assert.assertTrue(check);
    }

    @Test
    public void getAllAccounts(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
        }
        String response1 = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();

        JSONObject json2 = new JSONObject();
        try{
            json2.put("username","Acount02");
            json2.put("password","Acount02");
            json2.put("nickname","Acount02");
            json2.put("email","Acount02@gmail.com");
            json2.put("enabled",true);
            json2.put("systemrole",false);
        } catch (JSONException e){
        }
        String response2 = given().header("Authorization", token).port(port).contentType("application/json").body(json2.toString())
                .when().post("/accounts/add").andReturn().asString();

        String Actual = given().header("Authorization", token).port(port).contentType("application/json")
                .when().get("/accounts/all").andReturn().asString();
        System.out.println("Actual = " + Actual);
        try{
            JSONObject Accounts = new JSONObject(Actual);
            JSONArray ActualArray = new JSONArray(Accounts.get("accounts").toString());
            Assert.assertEquals("admin", ActualArray.optJSONObject(0).get("username").toString());
            Assert.assertEquals("admin", ActualArray.optJSONObject(0).get("nickname").toString());
            Assert.assertEquals("Scrumteam@ezScrum.com.tw", ActualArray.optJSONObject(0).get("email").toString());
            String checkStr = ActualArray.optJSONObject(0).get("enabled").toString();
            boolean checkEnable = Boolean.valueOf(checkStr);
            Assert.assertTrue(checkEnable);
            checkStr = ActualArray.optJSONObject(0).get("systemrole").toString();
            checkEnable = Boolean.valueOf(checkStr);
            Assert.assertTrue(checkEnable);
            checkStr = ActualArray.optJSONObject(0).get("id").toString();
            long checkId = Long.valueOf(checkStr);
            long id = Long.valueOf(response1) -1;
            Assert.assertEquals(id, checkId);

            Assert.assertEquals("Acount01", ActualArray.optJSONObject(1).get("username").toString());
            Assert.assertEquals("Acount01", ActualArray.optJSONObject(1).get("nickname").toString());
            Assert.assertEquals("Acount01@gmail.com", ActualArray.optJSONObject(1).get("email").toString());
            checkStr = ActualArray.optJSONObject(1).get("enabled").toString();
            checkEnable = Boolean.valueOf(checkStr);
            Assert.assertTrue(checkEnable);
            checkStr = ActualArray.optJSONObject(1).get("systemrole").toString();
            checkEnable = Boolean.valueOf(checkStr);
            Assert.assertFalse(checkEnable);
            checkStr = ActualArray.optJSONObject(1).get("id").toString();
            checkId = Long.valueOf(checkStr);
            id = Long.valueOf(response1);
            Assert.assertEquals(id, checkId);

            Assert.assertEquals("Acount02", ActualArray.optJSONObject(2).get("username").toString());
            Assert.assertEquals("Acount02", ActualArray.optJSONObject(2).get("nickname").toString());
            Assert.assertEquals("Acount02@gmail.com", ActualArray.optJSONObject(2).get("email").toString());
            checkStr = ActualArray.optJSONObject(2).get("enabled").toString();
            checkEnable = Boolean.valueOf(checkStr);
            Assert.assertTrue(checkEnable);
            checkStr = ActualArray.optJSONObject(2).get("systemrole").toString();
            checkEnable = Boolean.valueOf(checkStr);
            Assert.assertFalse(checkEnable);
            checkStr = ActualArray.optJSONObject(2).get("id").toString();
            checkId = Long.valueOf(checkStr);
            id = Long.valueOf(response2);
            Assert.assertEquals(id, checkId);
        }catch (JSONException e){
            e.printStackTrace();
        }
        userService.delete(Long.valueOf(response1));
        userService.delete(Long.valueOf(response2));
    }

    @Test
    public void getAllAccountList(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
        }
        String response1 = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();

        JSONObject json2 = new JSONObject();
        try{
            json2.put("username","Acount02");
            json2.put("password","Acount02");
            json2.put("nickname","Acount02");
            json2.put("email","Acount02@gmail.com");
            json2.put("enabled",true);
            json2.put("systemrole",false);
        } catch (JSONException e){
        }
        String response2 = given().header("Authorization", token).port(port).contentType("application/json").body(json2.toString())
                .when().post("/accounts/add").andReturn().asString();
        JSONObject getAccountList = new JSONObject();

        List<Long> ids = new ArrayList();
        long id1 = Long.valueOf(response1);
        long id2 = Long.valueOf(response2);
        try{
            getAccountList.put("accounts_id", new JSONArray(new Object[]{id1, id2}));
            System.out.println(getAccountList);
        }catch (JSONException e){
            e.printStackTrace();
        }
        String response = given().header("Authorization", token).port(port).contentType("application/json").body(getAccountList.toString())
                .when().post("/accounts/getAccountList").andReturn().asString();
        System.out.println("response** = " + response);
        try{
            JSONObject jsonGet = new JSONObject(response);
            System.out.println("jsonGet = " + jsonGet);
            Assert.assertEquals("Acount01", jsonGet.get(response1).toString());
            Assert.assertEquals("Acount02", jsonGet.get(response2).toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        userService.delete(Long.valueOf(response1));
        userService.delete(Long.valueOf(response2));
    }
    @Test
    public void getNotificationStatus_ConnectFail(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
            e.printStackTrace();
        }
        String response1 = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        String firebaseToken = "illegeal";
        long id = Long.valueOf(response1);
        JSONObject jsonGetNotificationStatus = new JSONObject();
        try{
            jsonGetNotificationStatus.put("account_id", id);
            jsonGetNotificationStatus.put("firebaseToken", firebaseToken);

        }catch (JSONException e){
            e.printStackTrace();
        }
        String result = given().header("Authorization", token).port(port).contentType("application/json").body(jsonGetNotificationStatus.toString())
                .when().post("/accounts/getNotificationSubscriptStatus").andReturn().asString();

        Assert.assertEquals("Connection Error", result);
        userService.delete(id);
    }

    @Test
    public void getNotificationStatus_IdIncorrect(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
            e.printStackTrace();
        }
        String response1 = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        String firebaseToken = "illegeal";
        long id = Long.valueOf(response1)+1;
        JSONObject jsonGetNotificationStatus = new JSONObject();
        try{
            jsonGetNotificationStatus.put("account_id", id);
            jsonGetNotificationStatus.put("firebaseToken", firebaseToken);

        }catch (JSONException e){
            e.printStackTrace();
        }
        String result = given().header("Authorization", token).port(port).contentType("application/json").body(jsonGetNotificationStatus.toString())
                .when().post("/accounts/getNotificationSubscriptStatus").andReturn().asString();

        Assert.assertEquals("This user is not exist.", result);
        userService.delete(id-1);
    }

    @Test
    public void subscribeNotification_ConnectFail(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
            e.printStackTrace();
        }
        String response1 = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        String firebaseToken = "illegeal";
        long id = Long.valueOf(response1);
        JSONObject jsonGetNotificationStatus = new JSONObject();
        try{
            jsonGetNotificationStatus.put("account_id", id);
            jsonGetNotificationStatus.put("firebaseToken", firebaseToken);

        }catch (JSONException e){
            e.printStackTrace();
        }
        String result = given().header("Authorization", token).port(port).contentType("application/json").body(jsonGetNotificationStatus.toString())
                .when().post("/accounts/subscribeNotification").andReturn().asString();

        Assert.assertEquals("Connection Error", result);
        userService.delete(id);
    }

    @Test
    public void subscribeNotification_IdIncorrect(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
            e.printStackTrace();
        }
        String response1 = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        String firebaseToken = "illegeal";
        long id = Long.valueOf(response1)+1;
        JSONObject jsonGetNotificationStatus = new JSONObject();
        try{
            jsonGetNotificationStatus.put("account_id", id);
            jsonGetNotificationStatus.put("firebaseToken", firebaseToken);

        }catch (JSONException e){
            e.printStackTrace();
        }
        String result = given().header("Authorization", token).port(port).contentType("application/json").body(jsonGetNotificationStatus.toString())
                .when().post("/accounts/subscribeNotification").andReturn().asString();

        Assert.assertEquals("This user is not exist.", result);
        userService.delete(id-1);
    }

    @Test
    public void unSubscribeNotification_ConnectFail(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
            e.printStackTrace();
        }
        String response1 = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        String firebaseToken = "illegeal";
        long id = Long.valueOf(response1);
        JSONObject jsonGetNotificationStatus = new JSONObject();
        try{
            jsonGetNotificationStatus.put("account_id", id);
            jsonGetNotificationStatus.put("firebaseToken", firebaseToken);

        }catch (JSONException e){
            e.printStackTrace();
        }
        String result = given().header("Authorization", token).port(port).contentType("application/json").body(jsonGetNotificationStatus.toString())
                .when().post("/accounts/cancelSubscribeNotification").andReturn().asString();

        Assert.assertEquals("Connection Error", result);
        userService.delete(id);
    }

    @Test
    public void unSubscribeNotification_IncorrectId(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
            e.printStackTrace();
        }
        String response1 = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        String firebaseToken = "illegeal";
        long id = Long.valueOf(response1)+1;
        JSONObject jsonGetNotificationStatus = new JSONObject();
        try{
            jsonGetNotificationStatus.put("account_id", id);
            jsonGetNotificationStatus.put("firebaseToken", firebaseToken);

        }catch (JSONException e){
            e.printStackTrace();
        }
        String result = given().header("Authorization", token).port(port).contentType("application/json").body(jsonGetNotificationStatus.toString())
                .when().post("/accounts/cancelSubscribeNotification").andReturn().asString();

        Assert.assertEquals("This user is not exist.", result);
        userService.delete(id-1);
    }

    @Test
    public void notifyServiceLogout_ConnectrionFail(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
            e.printStackTrace();
        }
        String response1 = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        String firebaseToken = "illegeal";
        long id = Long.valueOf(response1);
        JSONObject jsonGetNotificationStatus = new JSONObject();
        try{
            jsonGetNotificationStatus.put("account_id", id);
            jsonGetNotificationStatus.put("firebaseToken", firebaseToken);

        }catch (JSONException e){
            e.printStackTrace();
        }
        String result = given().header("Authorization", token).port(port).contentType("application/json").body(jsonGetNotificationStatus.toString())
                .when().post("/accounts/notifyServiceLogout").andReturn().asString();

        Assert.assertEquals("Connection Error", result);
        userService.delete(id);
    }

    @Test
    public void notifyServiceLogout_IncorrectId(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
            e.printStackTrace();
        }
        String response1 = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        String firebaseToken = "illegeal";
        long id = Long.valueOf(response1)+1;
        JSONObject jsonGetNotificationStatus = new JSONObject();
        try{
            jsonGetNotificationStatus.put("account_id", id);
            jsonGetNotificationStatus.put("firebaseToken", firebaseToken);

        }catch (JSONException e){
            e.printStackTrace();
        }
        String result = given().header("Authorization", token).port(port).contentType("application/json").body(jsonGetNotificationStatus.toString())
                .when().post("/accounts/notifyServiceLogout").andReturn().asString();

        Assert.assertEquals("This user is not exist.", result);
        userService.delete(id-1);
    }

    @Test
    public void sendNotification_NoId(){
        JSONArray accountArray = new JSONArray();
        JSONObject json = new JSONObject();
        String title = "illegal";
        String body = "illegal";
        String eventSource = "illegal";
        try{
            json.put("accounts_id", accountArray.toString());
            json.put("title", title);
            json.put("body", body);
            json.put("eventSource", eventSource);
        }catch (JSONException e){
            e.printStackTrace();
        }
        String result = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/sendNotification").andReturn().asString();

        Assert.assertEquals("Didn't have recipient.", result);
    }
    @Test
    public void sendNotification_ConnectionFail(){
        JSONObject json = new JSONObject();
        try{
            json.put("username","Acount01");
            json.put("password","Acount01");
            json.put("nickname","Acount01");
            json.put("email","Acount01@gmail.com");
            json.put("enabled",true);
            json.put("systemrole",false);
        } catch (JSONException e){
            e.printStackTrace();
        }
        String response1 = given().header("Authorization", token).port(port).contentType("application/json").body(json.toString())
                .when().post("/accounts/add").andReturn().asString();
        String firebaseToken = "illegeal";
        long id = Long.valueOf(response1);
        String title = "illegal";
        String body = "illegal";
        String eventSource = "illegal";
        JSONObject jsonArray = new JSONObject();
        List<Long> ids = new ArrayList();
        ids.add(id);
        JSONArray temp = new JSONArray(ids);
        try{
            jsonArray.put("accounts_id",temp.toString());
            jsonArray.put("title", title);
            jsonArray.put("body", body);
            jsonArray.put("eventSource", eventSource);
        }catch (JSONException e){
            e.printStackTrace();
        }
        String result = given().header("Authorization", token).port(port).contentType("application/json").body(jsonArray.toString())
                .when().post("/accounts/sendNotification").andReturn().asString();

        Assert.assertEquals("Connection Error", result);
        userService.delete(id);
    }
}
