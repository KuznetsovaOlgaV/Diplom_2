package ru.education_services.stellarburgers;

import io.restassured.RestAssured;
import org.junit.After;

public abstract class ApiTestBase {

    protected static final String BASE_URI = "https://stellarburgers.education-services.ru";

    protected final UserApi userApi = new UserApi();
    protected final OrderApi orderApi = new OrderApi();

    static {
        RestAssured.baseURI = BASE_URI;
    }

    @After
    public void baseCleanup() {
        try {
            userApi.deleteCurrentUser();
        } catch (Exception ignored) {
        }
        userApi.clearTokens();
    }
}