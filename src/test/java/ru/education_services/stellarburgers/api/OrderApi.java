package ru.education_services.stellarburgers.api;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.education_services.stellarburgers.request_model.CreateOrderRequest;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderApi {

    @Step("Создание заказа")
    public Response createOrder(String accessToken, List<String> ingredients) {
        CreateOrderRequest body = new CreateOrderRequest(ingredients);
        RequestSpecification request = given()
                .contentType(ContentType.JSON)
                .body(body);

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