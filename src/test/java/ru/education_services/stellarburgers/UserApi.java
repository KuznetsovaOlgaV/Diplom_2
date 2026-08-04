package ru.education_services.stellarburgers;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.*;

public class UserApi {
    private String accessToken;
    private String refreshToken;
    private String lastCreatedEmail;

    public Response registerUser(String email, String password, String name) {
        lastCreatedEmail = email;

        String safeEmail = (email == null) ? "" : email;
        String safePassword = (password == null) ? "" : password;
        String safeName = (name == null) ? "" : name;

        return given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", safeEmail, "password", safePassword, "name", safeName))
                .post("/api/auth/register")
                .then()
                .extract()
                .response();
    }

    public Response loginUser(String email, String password) {
        lastCreatedEmail = email;

        Response response = given()
                .contentType("application/json")
                .body(Map.of("email", email, "password", password))
                .post("/api/auth/login")
                .then()
                .extract()
                .response();

        if (response.statusCode() == 200) {
            String rawToken = response.jsonPath().getString("accessToken");

            if (rawToken != null && rawToken.startsWith("Bearer ")) {
                rawToken = rawToken.substring("Bearer ".length());
            } else if (rawToken != null && rawToken.startsWith("bearer ")) {
                rawToken = rawToken.substring("bearer ".length());
            }

            this.accessToken = rawToken;
            this.refreshToken = response.jsonPath().getString("refreshToken");
        }
        return response;
    }

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
}