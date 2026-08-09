package ru.education_services.stellarburgers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserApi {

    private final ObjectMapper mapper = new ObjectMapper();
    private String accessToken;   // хранится БЕЗ префикса Bearer
    private String refreshToken;
    private String lastCreatedEmail;

    @Step("Регистрация пользователя")
    public Response registerUser(String email, String password, String name) {
        lastCreatedEmail = email;
        Map<String, String> body = new HashMap<>();
        body.put("email", email == null ? "" : email);
        body.put("password", password == null ? "" : password);
        body.put("name", name == null ? "" : name);

        String jsonBody = serialize(body);
        Response response = given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
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
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        String jsonBody = serialize(body);
        Response response = given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
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

    private String serialize(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object", e);
        }
    }
}
