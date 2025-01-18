package com.solvd.api;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class BaseApiTest {

    protected ValidatableResponse assertStatusCode(Response response, int statusCode) {
        return response
                .then()
                .log()
                .ifValidationFails()
                .assertThat()
                .statusCode(statusCode);
    }
}
