package com.solvd.api;

import com.solvd.api.model.GraphQlQuery;
import com.solvd.api.model.User;
import com.solvd.api.utils.Files;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static com.solvd.api.utils.Constants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GraphqlTest {

    private static RequestSpecification requestSpec;
    private static User user;

    @BeforeMethod
    public void setUp() {
        requestSpec = given()
                .baseUri(BASE_URL + GRAPHQL_ENDPOINT)
                .contentType(ContentType.JSON)
                .auth()
                .oauth2(TOKEN)
                .log().all();
    }

    @Test
    public void QueryUserById() {

        String query = "query User($id: ID!) {\n" +
                "    user(id: $id) {\n" +
                "        name\n" +
                "        email\n" +
                "        gender\n" +
                "        id\n" +
                "        status\n" +
                "    }\n" +
                "}";

        GraphQlQuery requestBody = new GraphQlQuery();
        requestBody.setQuery(query);
        requestBody.setVariables(Map.entry("id", 7636782));

        Response response = requestSpec
                .body(requestBody)
                .post();

        response.then().log().ifValidationFails().assertThat().statusCode(200);
        response.then().log().ifValidationFails().body("data.user.name", equalTo("Mark"));
        User user = response.body().jsonPath().getObject("data.user", User.class);
        Assert.assertEquals(user.getName(), "Mark", "The expected name is not present");

        //print response body
        response.prettyPrint();
    }

    @Test
    public void CreateAUser() {

        user = Files.getAUserFromAJsonFile("create-user.json");

        String query = "mutation CreateUser($name: String!, $email: String!, $gender: String!, $status: String!) {" +
                "    createUser(" +
                "        input: {" +
                "            name: $name " +
                "            email: $email " +
                "            gender: $gender " +
                "            status: $status " +
                "            clientMutationId: \"123123123\"" +
                "        }" +
                "    ) {" +
                "        clientMutationId" +
                " user { email name status id gender } " +
                "    }" +
                "}";

        GraphQlQuery requestBody = new GraphQlQuery();
        requestBody.setQuery(query);
        requestBody.setVariables(user);

        Response response = requestSpec
                .body(requestBody)
                .post();

        response.then()
                .log()
                .ifValidationFails()
                .assertThat()
                .statusCode(200)
                .body("data.createUser.user.name", equalTo(user.getName()));

        user = response.body().jsonPath().getObject("data.createUser.user", User.class);
        //print response body
        response.prettyPrint();
    }

    @Test
    public void QueryAllUsers() {
        String query = "query Users {\n" +
                "    users {\n" +
                "        nodes {\n" +
                "            email\n" +
                "            gender\n" +
                "            id\n" +
                "            name\n" +
                "            status\n" +
                "        }\n" +
                "    }\n" +
                "}\n";

        GraphQlQuery requestBody = new GraphQlQuery();
        requestBody.setQuery(query);

        Response response = requestSpec
                .body(requestBody)
                .post();

        response.then().log().ifValidationFails().assertThat().statusCode(200);
        List<User> userList = response.body().jsonPath().getList("data.users.nodes", User.class);
        Assert.assertFalse(userList.isEmpty(), "User list is empty");

        //print response body
        response.prettyPrint();
    }

    @Test
    public void CreateAUserWithAnInvalidEmailFormat() {

        User user = Files.getAUserFromAJsonFile("create-user.json");

        String query = "mutation CreateUser($name: String!, $email: String!, $gender: String!, $status: String!) {" +
                "    createUser(" +
                "        input: {" +
                "            name: $name " +
                "            email: $email " +
                "            gender: $gender " +
                "            status: $status " +
                "            clientMutationId: \"123123123\"" +
                "        }" +
                "    ) {" +
                "        clientMutationId" +
                "    }" +
                "}";

        GraphQlQuery requestBody = new GraphQlQuery();
        requestBody.setQuery(query);
        //set email with an invalid format
        user.setEmail(INVALID_EMAIL);
        requestBody.setVariables(user);

        Response response = requestSpec
                .body(requestBody)
                .post();

        response
                .then()
                .log()
                .ifValidationFails()
                .assertThat()
                .statusCode(200)
                .body("errors[0].extensions.result[0].fieldName", equalTo("email"))
                .body("errors[0].extensions.result[0].messages[0]", equalTo(IS_INVALID));


        //print response body
        response.prettyPrint();
    }

    @Test(dependsOnMethods = {"CreateAUser"})
    public void DeleteAUser() {

        String query = "mutation DeleteUser($id: ID!) {\n" +
                "    deleteUser(input: { id: $id }) {\n" +
                "        clientMutationId\n" +
                "    }\n" +
                "}";

        GraphQlQuery requestBody = new GraphQlQuery();
        requestBody.setQuery(query);
        requestBody.setVariables(Map.entry("id", user.getId()));

        Response response = requestSpec
                .body(requestBody)
                .post();

        response
                .then()
                .log()
                .ifValidationFails()
                .assertThat()
                .statusCode(200);


    }

}
