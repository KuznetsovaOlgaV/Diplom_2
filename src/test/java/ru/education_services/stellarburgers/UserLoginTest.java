package ru.education_services.stellarburgers;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import ru.education_services.stellarburgers.api.ApiTestBase;

import java.util.UUID;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class UserLoginTest extends ApiTestBase {

    private String email;
    private String password;
    private String name;

    @Before
    public void setUp() {
        email = "login-" + UUID.randomUUID() + "@yandex.ru";
        password = "StrongPassword1234";
        name = "Login User";

        userApi.registerUser(email, password, name)
                .then()
                .statusCode(SC_OK);
    }

    @Test
    @Description("Логин пользователя: вход под существующим пользователем")
    public void testSuccessfulLogin() {
        Response response = userApi.loginUser(email, password);
        response.then()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @Description("Логин пользователя: вход с неверным паролем")
    public void testLoginWithInvalidPassword() {
        Response response = userApi.loginUser(email, "wrongpassword");
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @Description("Логин пользователя: вход с неверным логином (несуществующий пользователь)")
    public void testLoginWithInvalidLogin() {
        Response response = userApi.loginUser("nonexistent@yandex.ru", password);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}