package com.solvd.api;

import com.solvd.api.model.User;
import com.solvd.api.service.RestAPIRequests;
import com.solvd.api.utils.Files;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static com.solvd.api.utils.Constants.*;
import static org.hamcrest.Matchers.equalTo;

public class RestApiTest extends BaseApiTest {

    private static RestAPIRequests requests;

    @BeforeClass
    public void setUp() {
        requests = new RestAPIRequests();
    }

    @Test
    public void verifyGetAllUsersTest() {
        Response response = requests.getAllUsers();
        assertStatusCode(response, 200);
        int userListSize = response.body().jsonPath().getList("", User.class).size();
        Assert.assertTrue(userListSize >= 10, "List size is less than 10: " + userListSize);
        response.body().prettyPrint();
    }

    @Test
    public void verifyGetAUserByIdTest() {
        User user = createAUser();
        Response response = requests.getUserById(user.getId());
        assertStatusCode(response, 200)
                .body("id", equalTo(user.getId()));
        response.body().prettyPrint();
    }

    @Test
    public void verifyCreateAUserWithoutSendingBearerTokenTest() {
        Response response = requests.createAUserWithoutSendingBearerToken();
        assertStatusCode(response, 401)
                .body("message", equalTo(AUTHENTICATION_FAILED));
        response.body().prettyPrint();
    }

    @Test
    public void verifyCreateAUserTest() {
        User user = Files.getAUserFromAJsonFile("create-user.json");
        Response response = requests.createAUser(user);
        assertStatusCode(response, 201);
        User resUser = response.body().as(User.class);
        Assert.assertTrue(resUser.getId() > 0, "Invalid id");
        Assert.assertEquals(resUser.getName(), user.getName(), "Response name is not the expected!");
        Assert.assertEquals(resUser.getEmail(), user.getEmail(), "Response email is not the expected!");
        Assert.assertEquals(resUser.getGender(), user.getGender(), "Response gender is not the expected!");
        Assert.assertEquals(resUser.getStatus(), user.getStatus(), "Response status is not the expected!");
        response.body().prettyPrint();
    }

    @Test
    public void verifyPartiallyModifyAUserTest() {
        User user = createAUser();
        Map.Entry<String, String> name = Map.entry("name", "Joe");
        Response response = requests.partiallyModifyAUser(user.getId(), name);
        assertStatusCode(response, 200)
                .body("name", equalTo(name.getValue()));
        response.body().prettyPrint();
    }

    @Test
    public void verifyFullyModifyAUserTest() {
        User user = createAUser();
        User modifiedUser = Files.getAUserFromAJsonFile("update-user.json");
        Response response = requests.fullyModifyAUser(user.getId(), modifiedUser);
        assertStatusCode(response, 200)
                .body("name", equalTo(modifiedUser.getName()));
        response.body().prettyPrint();
    }

    @Test
    public void verifyDeleteAUserTest() {
        User user = createAUser();
        Response response = requests.deleteAUser(user);
        assertStatusCode(response, 204);
        response.prettyPeek();
    }

    @Test
    public void verifyCreateAUserWithAnEmailThatIsAlreadyInUseTest() {
        User user = Files.getAUserFromAJsonFile("create-user-existent-email.json");
        Response response = requests.createAUser(user);
        ValidatableResponse validatableResponse = assertStatusCode(response, 422);
        validateErrorMessageOnEmailField(validatableResponse, ALREADY_BEEN_TAKEN);
        response.body().prettyPrint();
    }

    @Test
    public void verifyDeleteANonExistentUserTest() {
        User user = new User();
        user.setId(NON_EXISTENT_USER_ID);
        Response response = requests.deleteAUser(user);
        assertStatusCode(response, 404)
                .body("message", equalTo(RESOURCE_NOT_FOUND));
        response.body().prettyPrint();
    }

    @Test
    public void verifyCreateAUserWithAnInvalidEmailFormatTest() {
        User user = Files.getAUserFromAJsonFile("create-user.json");
        user.setEmail(INVALID_EMAIL);
        Response response = requests.createAUser(user);
        ValidatableResponse validatableResponse = assertStatusCode(response, 422);
        validateErrorMessageOnEmailField(validatableResponse, IS_INVALID);
        response.body().prettyPrint();
    }

    //helper methods
    private static User createAUser() {
        User user = Files.getAUserFromAJsonFile("create-user.json");
        user = requests.createAUser(user).as(User.class);
        return user;
    }

    private static void validateErrorMessageOnEmailField(ValidatableResponse validatableResponse, String message) {
        validatableResponse
                .body("[0].field", equalTo("email"))
                .body("[0].message", equalTo(message));
    }
}
