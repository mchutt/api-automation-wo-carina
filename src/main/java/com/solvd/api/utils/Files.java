package com.solvd.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solvd.api.model.User;

import java.io.File;
import java.io.IOException;

public class Files {

    public static User getAUserFromAJsonFile(String fileName) {
        File file = new File(System.getProperty("user.dir") + "/src/test/resources/rq/user/" + fileName);
        ObjectMapper mapper = new ObjectMapper();
        User user;
        try {
            user = mapper.readValue(file, User.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        replaceEmailPlaceholderWithARandomString(user);

        return user;
    }

    private static void replaceEmailPlaceholderWithARandomString(User user) {
        String modifiedEmail = user.getEmail().replace("{{random_string}}", StringUtils.getSaltString());
        user.setEmail(modifiedEmail);
    }


}
