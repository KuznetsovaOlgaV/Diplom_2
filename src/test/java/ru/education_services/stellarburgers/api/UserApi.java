package ru.education_services.stellarburgers.api;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.education_services.stellarburgers.request_model.LoginRequest;
import ru.education_services.stellarburgers.request_model.RegisterRequest;

import static io.restassured.RestAssured.given;

public class UserApi {
    private String accessToken;
    private String refreshToken;
    private String lastCreatedEmail;

    @Step("Регистрация пользователя")
    public Response registerUser(String email, String password, String name) {
        lastCreatedEmail = email;
        RegisterRequest body = new RegisterRequest(email, password, name);
        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("/api/auth/register")
                .then()
                .extract()
                .response();
        if (response.statusCode() == 200) {
            this.accessToken = extractAccessToken(response);
            this.refreshToken = response.jsonPath().getString("refreshToken");
        }
        return response;
    }

    @Step("Логин пользователя")
    public Response loginUser(String email, String password) {
        lastCreatedEmail = email;
        LoginRequest body = new LoginRequest(email, password);
        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("/api/auth/login")
                .then()
                .extract()
                .response();
        if (response.statusCode() == 200) {
            this.accessToken = extractAccessToken(response);
            this.refreshToken = response.jsonPath().getString("refreshToken");
        }
        return response;
    }

    @Step("Удаление текущего пользователя")
    public Response deleteCurrentUser() {
        if (accessToken == null || accessToken.isEmpty()) {
            return null;
        }
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .delete("/api/auth/user")
                .then()
                .extract()
                .response();
    }

    public void clearTokens() {
        this.accessToken = null;
        this.refreshToken = null;
    }

    public String getLastCreatedEmail() {
        return lastCreatedEmail;
    }

    public String getAccessToken() {
        return accessToken;
    }

    private String extractAccessToken(Response response) {
        String rawToken = response.jsonPath().getString("accessToken");
        if (rawToken != null && rawToken.startsWith("Bearer ")) {
            return rawToken.substring("Bearer ".length());
        }
        return rawToken;
    }
}