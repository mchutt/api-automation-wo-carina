package com.solvd.api;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class RestApiTest {

    private static final String baseUrl = "https://gorest.co.in/public/v2";

    @Test
    public void getAllUsers() {
        get(baseUrl + "/users").prettyPrint();
    }
}
