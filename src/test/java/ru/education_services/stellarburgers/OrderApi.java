package ru.education_services.stellarburgers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderApi {

    private final ObjectMapper mapper = new ObjectMapper();

    @Step("Создание заказа")
    public Response createOrder(String accessToken, List<String> ingredients) {
        Map<String, List<String>> body = new HashMap<>();
        body.put("ingredients", ingredients != null ? ingredients : List.of());

        String jsonBody;
        try {
            jsonBody = mapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize order request", e);
        }

        var request = given()
                .contentType(ContentType.JSON)
                .body(jsonBody);

        if (accessToken != null && !accessToken.isEmpty()) {
            request.header("Authorization", "Bearer " + accessToken);
        }

        return request
                .post("/api/orders")
                .then()
                .extract()
                .response();
    }
}
