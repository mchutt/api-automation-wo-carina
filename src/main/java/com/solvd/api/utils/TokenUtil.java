package com.solvd.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TokenUtil {

    public static String getToken() throws IOException {
        Properties properties = new Properties();

        // Load the properties file
        try (InputStream input = TokenUtil.class.getClassLoader().getResourceAsStream("app.properties")) {
            if (input == null) {
                throw new IOException("Unable to find app.properties file in the classpath.");
            }

            properties.load(input);
        }

        // Retrieve the token from the properties file
        String token = properties.getProperty("token");

        if (token == null || token.isEmpty()) {
            throw new IOException("Token is not defined in the app.properties file.");
        }

        return token;
    }
}
