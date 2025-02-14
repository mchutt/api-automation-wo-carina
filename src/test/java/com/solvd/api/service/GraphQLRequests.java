package com.solvd.api.service;

import com.solvd.api.model.GraphQlQuery;
import com.solvd.api.model.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static com.solvd.api.utils.Constants.*;
import static io.restassured.RestAssured.given;

public class GraphQLRequests {

    public Response deleteAUser(User user) {
        String query = "mutation DeleteUser($id: Int!) { deleteUser(input: { id: $id }) { clientMutationId } }";
        GraphQlQuery requestBody = getGraphQlQuery(query, Map.entry("id", user.getId()));
        return sendRequest(requestBody);
    }

    public Response queryUserById(String id) {
        String query = "query User($id: ID!) { user(id: $id) { name email gender id status } }";
        GraphQlQuery requestBody = getGraphQlQuery(query, Map.entry("id", id));
        return sendRequest(requestBody);
    }

    public Response createAUser(User user) {
        String query = "mutation CreateUser($name: String!, $email: String!, $gender: String!, $status: String!) { createUser(input: { name: $name, email: $email, gender: $gender, status: $status, clientMutationId: \"123123123\" }) { clientMutationId user { email name status id gender } } }";
        GraphQlQuery requestBody = getGraphQlQuery(query, user);
        return sendRequest(requestBody);
    }

    public Response getAllUsers() {
        String query = "query Users { users { nodes { email gender id name status } } }";
        GraphQlQuery requestBody = getGraphQlQuery(query);
        return sendRequest(requestBody);
    }

    public Response sendRequest(GraphQlQuery requestBody) {
        return given()
                .baseUri(BASE_URL + GRAPHQL_ENDPOINT)
                .contentType(ContentType.JSON)
                .auth().oauth2(TOKEN)
                .log().all()
                .body(requestBody)
                .post();
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
