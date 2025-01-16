package com.solvd.api;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static com.solvd.api.utils.Constants.*;
import static io.restassured.RestAssured.given;

public class BaseApiTest {

    protected RequestSpecification getRequest() {
        return given()
                .baseUri(BASE_URL + GRAPHQL_ENDPOINT)
                .contentType(ContentType.JSON)
                .auth()
                .oauth2(TOKEN)
                .log().all();
    }
}
