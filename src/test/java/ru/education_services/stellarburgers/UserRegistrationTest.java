package ru.education_services.stellarburgers;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Test;
import ru.education_services.stellarburgers.api.ApiTestBase;

import java.util.UUID;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class UserRegistrationTest extends ApiTestBase {

    @Test
    @Description("Создание пользователя: создать уникального пользователя")
    public void testSuccessfulRegistration() {
        String email = "reg-" + UUID.randomUUID() + "@yandex.ru";
        String password = "StrongPassword1234";
        String name = "Test User";

        Response response = userApi.registerUser(email, password, name);
        response.then()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("user.email", is(email))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @Description("Создание пользователя: создать пользователя, который уже зарегистрирован")
    public void testExistingUserRegistration() {
        String email = "exist-" + UUID.randomUUID() + "@yandex.ru";
        String password = "Password1234";
        String name = "Existing User";

        userApi.registerUser(email, password, name);
        Response response = userApi.registerUser(email, password, name);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @Description("Создание пользователя: без обязательного поля email")
    public void testRegistrationWithoutEmail() {
        Response response = userApi.registerUser(null, "Password1234", "Name");
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @Description("Создание пользователя: без обязательного поля password")
    public void testRegistrationWithoutPassword() {
        Response response = userApi.registerUser("test@yandex.ru", null, "Name");
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @Description("Создание пользователя: без обязательного поля name")
    public void testRegistrationWithoutName() {
        Response response = userApi.registerUser("test@yandex.ru", "Password1234", null);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}