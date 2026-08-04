package ru.education_services.stellarburgers;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.*;

import io.qameta.allure.restassured.AllureRestAssured;

public class UserRegistrationTest extends ApiTestBase {

    @Test
    @Description("Создание пользователя: создать уникального пользователя")
    public void testSuccessfulRegistration() {
        String email = "reg-" + UUID.randomUUID() + "@yandex.ru";
        String password = "StrongPassword1234";
        String name = "Test User";


        Response response = userApi.registerUser(email, password, name);

        response.then()
                .statusCode(200)
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
                .statusCode(403)
                .body("success", is(false));
    }

    @Test
    @Description("Создание пользователя: создать пользователя и не заполнить одно из обязательных полей - без email")
    public void testRegistrationWithoutEmail() {
        Response response = userApi.registerUser(null, "Password1234", "Name");
        response.then().statusCode(403).body("success", is(false));
    }

    @Test
    @Description("Создание пользователя: создать пользователя и не заполнить одно из обязательных полей - без пароля")
    public void testRegistrationWithoutPassword() {
        Response response = userApi.registerUser("test@yandex.ru", null, "Name");
        response.then().statusCode(403).body("success", is(false));
    }

    @Test
    @Description("Создание пользователя: создать пользователя и не заполнить одно из обязательных полей - без имени")
    public void testRegistrationWithoutName() {
        Response response = userApi.registerUser("test@yandex.ru", "Password1234", null);
        response.then().statusCode(403).body("success", is(false));
    }
}