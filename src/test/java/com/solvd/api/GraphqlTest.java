package com.solvd.api;

import com.solvd.api.model.User;
import com.solvd.api.service.GraphQLRequests;
import com.solvd.api.utils.Files;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.solvd.api.utils.Constants.*;
import static org.testng.Assert.assertEquals;

public class GraphqlTest extends BaseApiTest {

    private static GraphQLRequests requests;

    @BeforeClass
    public void setUp() {
        requests = new GraphQLRequests();
    }

    @Test
    public void verifyQueryUserByIdTest() {
        Response response = requests.queryUserById(ID);
        assertStatusCode(response, 200);
        assertEquals(response.jsonPath().getInt("data.user.id"), ID, "User ID does not match");
    }

    @Test
    public void verifyCreateAUserTest() {
        User user = Files.getAUserFromAJsonFile("create-user.json");
        Response response = requests.createAUser(user);
        assertStatusCode(response, 200);
        assertEquals(response.jsonPath().getString("data.createUser.user.name"), user.getName(), "User Name does not match");
    }

    @Test
    public void verifyQueryAllUsersTest() {
        Response response = requests.getAllUsers();
        assertStatusCode(response, 200);
        List<User> userList = response.body().jsonPath().getList("data.users.nodes", User.class);
        Assert.assertFalse(userList.isEmpty(), "User list is empty");
    }

    @Test
    public void verifyCreateAUserWithAnInvalidEmailFormatTest() {
        User user = Files.getAUserFromAJsonFile("create-user.json");
        user.setEmail(INVALID_EMAIL);
        Response response = requests.createAUser(user);
        assertStatusCode(response, 200);
        assertEquals(response.jsonPath().getString("errors[0].extensions.result[0].fieldName"), "email", "Field do not match");
        assertEquals(response.jsonPath().getString("errors[0].extensions.result[0].messages[0]"), IS_INVALID, "Error message does not match");
    }

    @Test
    public void verifyDeleteAUserTest() {
        User user = Files.getAUserFromAJsonFile("create-user.json");
        user = requests.createAUser(user).jsonPath().getObject("data.createUser.user", User.class);
        Response response = requests.deleteAUser(user);
        assertStatusCode(response, 200);
    }
}
