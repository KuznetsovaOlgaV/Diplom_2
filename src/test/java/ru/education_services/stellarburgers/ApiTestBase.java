package ru.education_services.stellarburgers;

import io.restassured.RestAssured;
import org.junit.After;


public abstract class ApiTestBase {

    protected static final String BASE_URI = "https://stellarburgers.education-services.ru";
    protected UserApi userApi = new UserApi();

    static {
        RestAssured.baseURI = BASE_URI;
    }


    @After
    public void cleanup() {
        try {
            userApi.deleteCurrentUser();
        } catch (Exception e) {
        }
        userApi.clearTokens();
    }
}