package com.solvd.api.utils;

import java.io.IOException;

import static com.solvd.api.utils.TokenUtil.getToken;

public class Constants {

    public static final String TOKEN;
    static {
        try {
            TOKEN = getToken();
        } catch (IOException e) {
            System.out.println("Token not found!" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public static final String BASE_URL = "https://gorest.co.in/public/v2";
    public static final String USERS_ENDPOINT = "/users";
    public static final String GRAPHQL_ENDPOINT = "/graphql";


    public static final String INVALID_EMAIL = "invalidemail";
    public static final int NON_EXISTENT_USER_ID = 1;
    public static final int ID = 7636782;


    // error messages
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String ALREADY_BEEN_TAKEN = "has already been taken";
    public static final String IS_INVALID = "is invalid";
    public static final String AUTHENTICATION_FAILED = "Authentication failed";
}
