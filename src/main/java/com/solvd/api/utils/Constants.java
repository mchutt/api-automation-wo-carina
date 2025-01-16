package com.solvd.api.utils;

public class Constants {

    // request info
    public static final String TOKEN = "4bee5262a4e3426a5e3473442e31be4d23163e870a900b66b8366fbcd294bcdb"; //TODO get token from a config file
    public static final String BASE_URL = "https://gorest.co.in/public/v2";
    public static final String USERS_ENDPOINT = "/users";
    public static final String GRAPHQL_ENDPOINT = "/graphql";

    // data
    public static final String INVALID_EMAIL = "invalidemail";


    // response messages
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String ALREADY_BEEN_TAKEN = "has already been taken";
    public static final String IS_INVALID = "is invalid";
    public static final String AUTHENTICATION_FAILED = "Authentication failed";
}
