package com.solvd.api.service;

import com.solvd.api.model.User;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static com.solvd.api.utils.Constants.*;
import static io.restassured.RestAssured.given;

public class RestAPIRequests {

    public Response partiallyModifyAUser(int id, Map.Entry<String, String> value) {
        return getRequest()
                .body(value)
                .patch(USERS_ENDPOINT + "/" + id);
    }

    public Response fullyModifyAUser(int userId, User user) {
        return getRequest()
                .body(user)
                .put(USERS_ENDPOINT + "/" + userId);
    }

    public Response getAllUsers() {
        return getRequest().get(USERS_ENDPOINT);
    }

    public Response createAUser(User user) {
        return getRequest()
                .body(user)
                .post(USERS_ENDPOINT);
    }

    public Response createAUserWithoutSendingBearerToken() {
        return getRequest()
                .auth().none()
                .body(new User())
                .post(USERS_ENDPOINT);
    }

    public Response deleteAUser(User user) {
        return getRequest()
                .body(user)
                .delete(USERS_ENDPOINT + "/" + user.getId());
    }

    public Response getUserById(int id) {
        return getRequest()
                .get(USERS_ENDPOINT + "/" + id);
    }

    public RequestSpecification getRequest() {
        return given()
                .baseUri(BASE_URL)
                .contentType("Application/json")
                .accept("Application/json")
                .auth()
                .oauth2(TOKEN)
                .log().all();
    }

}
