package ru.education_services.stellarburgers;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

import io.qameta.allure.restassured.AllureRestAssured;

public class UserLoginTest extends ApiTestBase {


    @After
    public void tearDown() {
        if (userApi != null && userApi.getLastCreatedEmail() != null) {
            Response deleteResponse = userApi.deleteCurrentUser();
            if (deleteResponse != null) {
            }
        }
        userApi.clearTokens();
    }

    @Test
    @Description("Логин пользователя: вход под существующим пользователем")
    public void testSuccessfulLogin() {
        String email = "login-" + System.currentTimeMillis() + "@yandex.ru";
        String password = "StrongPassword1234";
        String name = "Login User";

        Response registerResponse = userApi.registerUser(email, password, name);
        registerResponse.then()
                .statusCode(200)
                .body("success", is(true));

        Response response = userApi.loginUser(email, password);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @Description("Логин пользователя: вход с неверным логином и паролем - паролем")
    public void testInvalidCredentials() {
        String email = "wrong-" + System.currentTimeMillis() + "@yandex.ru";
        String password = "CorrectPassword1234";
        String name = "Wrong User";

        Response registerResponse = userApi.registerUser(email, password, name);
        registerResponse.then()
                .statusCode(200)
                .body("success", is(true));

        Response response = userApi.loginUser(email, "wrongpassword");

        response.then()
                .statusCode(401)
                .body("success", is(false));
    }

    @Test
    @Description("Логин пользователя: вход с неверным логином и паролем - логином ")
    public void testNonexistentUserLogin() {
        Response response = userApi.loginUser("nonexistent@yandex.ru", "anypassword");
        response.then().statusCode(401).body("success", is(false));
    }
}