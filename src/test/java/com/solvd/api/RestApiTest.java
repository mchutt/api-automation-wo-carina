package com.solvd.api;

import com.solvd.api.model.ErrorMessage;
import com.solvd.api.model.User;
import com.solvd.api.utils.Files;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static com.solvd.api.utils.Constants.*;
import static io.restassured.RestAssured.*;

public class RestApiTest {

    private static RequestSpecification requestSpec;
    private static User user;

    @BeforeMethod
    public void setUp() {
        requestSpec = given()
                .baseUri(BASE_URL)
                .contentType("Application/json")
                .accept("Application/json")
                .auth()
                .oauth2(TOKEN)
                .log().all();
    }


    @Test
    public void getAllUsers() {
        Response response = requestSpec.get(USERS_ENDPOINT);
        Assert.assertEquals(response.statusCode(), 200);
        List<User> userList = response.body().jsonPath().getList("", User.class);
        Assert.assertTrue(userList.size() >= 10, "List size is less than 10: " + userList.size());

        //print response body
        response.body().prettyPrint();
    }

    @Test(dependsOnMethods = {"CreateAUser"})
    public void getAUserById() {

        Response response = requestSpec.get(USERS_ENDPOINT + "/" + user.getId());
        Assert.assertEquals(response.statusCode(), 200);

        User resUser = response.as(User.class);
        Assert.assertEquals(resUser.getId(), user.getId(), "The user id is not as expected");

        //print response body
        response.body().prettyPrint();
    }

    @Test
    public void CreateAUserWithoutSendingBearerToken() {

        Response response = requestSpec
                .auth().none()
                .body(new User())
                .post(USERS_ENDPOINT);

        Assert.assertEquals(response.statusCode(), 401);
        ErrorMessage errorMessage = response.body().as(ErrorMessage.class);
        Assert.assertEquals(errorMessage.getMessage(), AUTHENTICATION_FAILED, "Error messages do not match");

        //print response body
        response.body().prettyPrint();
    }

    @Test
    public void CreateAUser() {

        user = Files.getAUserFromAJsonFile("create-user.json");

        Response response = requestSpec
                .body(user)
                .post(USERS_ENDPOINT);

        Assert.assertEquals(response.statusCode(), 201);
        User resUser = response.body().as(User.class);
        Assert.assertTrue(resUser.getId() > 0, "Invalid id");
        Assert.assertEquals(resUser.getName(), user.getName(), "Response name is not the expected!");
        Assert.assertEquals(resUser.getEmail(), user.getEmail(), "Response email is not the expected!");
        Assert.assertEquals(resUser.getGender(), user.getGender(), "Response gender is not the expected!");
        Assert.assertEquals(resUser.getStatus(), user.getStatus(), "Response status is not the expected!");

        user = resUser;

        //print response body
        response.body().prettyPrint();
    }


    @Test(dependsOnMethods = {"CreateAUser"})
    public void PartiallyModifyAUser() {
        //updated name
        Map.Entry<String, String> name = Map.entry("name", "Joe");

        Response response = requestSpec
                .body(name)
                .patch(USERS_ENDPOINT + "/" + user.getId());

        Assert.assertEquals(response.statusCode(), 200);
        User updatedUser = response.body().as(User.class);
        Assert.assertEquals(updatedUser.getName(), name.getValue());
        //validate that not-updated properties remain with their original values
        Assert.assertEquals(updatedUser.getId(), user.getId());
        Assert.assertEquals(updatedUser.getEmail(), user.getEmail());
        Assert.assertEquals(updatedUser.getStatus(), user.getStatus());
        Assert.assertEquals(updatedUser.getGender(), user.getGender());

        user = updatedUser;

        //print response body
        response.body().prettyPrint();

    }

    @Test(dependsOnMethods = {"CreateAUser", "PartiallyModifyAUser"})
    public void FullyModifyAUser() {
        int userId = user.getId();
        user = Files.getAUserFromAJsonFile("update-user.json");
        user.setId(userId);

        Response response = requestSpec
                .body(user)
                .put(USERS_ENDPOINT + "/" + userId);

        Assert.assertEquals(response.statusCode(), 200);
        User resUser = response.body().as(User.class);
        Assert.assertEquals(resUser, user);

        //print response body
        response.body().prettyPrint();
    }

    @Test(dependsOnMethods = {"CreateAUser", "PartiallyModifyAUser", "FullyModifyAUser"})
    public void DeleteAUser(){
        Response response = requestSpec
                .body(user)
                .delete(USERS_ENDPOINT + "/" + user.getId());

        Assert.assertEquals(response.statusCode(), 204);
    }

    @Test
    public void CreateAUserWithAnEmailThatIsAlreadyInUse() {
        User userFromFile = Files.getAUserFromAJsonFile("create-user-existent-email.json");

        Response response = requestSpec
                .body(userFromFile)
                .post(USERS_ENDPOINT);

        Assert.assertEquals(response.statusCode(), 422);
        ErrorMessage errorMessage = response.body().jsonPath().getList("", ErrorMessage.class).get(0);
        Assert.assertEquals(errorMessage.getField(), "email", "Error fields do not match");
        Assert.assertEquals(errorMessage.getMessage(), ALREADY_BEEN_TAKEN, "Error messages do not match");

        //print response body
        response.body().prettyPrint();
    }

    @Test
    public void deleteANonExistentUser() {
        int nonExistentUserId = 1;

        Response response = requestSpec
                .delete(USERS_ENDPOINT + "/" + nonExistentUserId);

        Assert.assertEquals(response.statusCode(), 404, "Response status codes do not match");
        ErrorMessage errorMessage = response.body().as(ErrorMessage.class);
        Assert.assertEquals(errorMessage.getMessage(), RESOURCE_NOT_FOUND, "Response error messages do not match");

        //print response body
        response.body().prettyPrint();
    }

    @Test
    public void CreateAUserWithAnInvalidEmailFormat() {
        User user = new User();
        user.setEmail(INVALID_EMAIL);
        user.setGender("male");
        user.setName("Pep");
        user.setStatus("active");

        Response response = requestSpec
                .body(user)
                .post(USERS_ENDPOINT);

        Assert.assertEquals(response.statusCode(), 422);
        ErrorMessage errorMessage = response.body().jsonPath().getList("", ErrorMessage.class).get(0);
        Assert.assertEquals(errorMessage.getField(), "email", "Error fields do not match");
        Assert.assertEquals(errorMessage.getMessage(), IS_INVALID, "Error messages do not match");

        //print response body
        response.body().prettyPrint();
    }
}
