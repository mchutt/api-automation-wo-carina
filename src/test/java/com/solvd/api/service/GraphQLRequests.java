package com.solvd.api.service;

import com.solvd.api.model.GraphQlQuery;
import com.solvd.api.model.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static com.solvd.api.utils.Constants.*;
import static io.restassured.RestAssured.given;

public class GraphQLRequests {
    public Response deleteAUser(User user) {
        String query = "mutation DeleteUser($id: Int!) {\n" +
                "    deleteUser(input: { id: $id }) {\n" +
                "        clientMutationId\n" +
                "    }\n" +
                "}";
        GraphQlQuery requestBody = getGraphQlQuery(query, Map.entry("id", user.getId()));
        return getRequest()
                .body(requestBody)
                .post();
    }

    public Response queryUserById(int id) {
        String query = "query User($id: ID!) {\n" +
                "    user(id: $id) {\n" +
                "        name\n" +
                "        email\n" +
                "        gender\n" +
                "        id\n" +
                "        status\n" +
                "    }\n" +
                "}";
        GraphQlQuery requestBody = getGraphQlQuery(query, Map.entry("id", id));
        return getRequest()
                .body(requestBody)
                .post();
    }

    public Response createAUser(User user) {
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
        GraphQlQuery requestBody = getGraphQlQuery(query, user);
        return getRequest()
                .body(requestBody)
                .post();

    }

    public Response getAllUsers() {
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
        GraphQlQuery requestBody = getGraphQlQuery(query);
        return getRequest()
                .body(requestBody)
                .post();
    }

    public RequestSpecification getRequest(){
         return given()
                .baseUri(BASE_URL + GRAPHQL_ENDPOINT)
                .contentType(ContentType.JSON)
                .auth()
                .oauth2(TOKEN)
                .log().all();
    }

    protected static GraphQlQuery getGraphQlQuery(String query) {
        return getGraphQlQuery(query, null);
    }

    protected static GraphQlQuery getGraphQlQuery(String query, Object variables) {
        GraphQlQuery requestBody = new GraphQlQuery();
        requestBody.setQuery(query);
        requestBody.setVariables(variables);
        return requestBody;
    }
}
