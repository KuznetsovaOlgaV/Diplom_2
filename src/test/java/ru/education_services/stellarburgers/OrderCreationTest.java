package ru.education_services.stellarburgers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

import io.qameta.allure.restassured.AllureRestAssured;

public class OrderCreationTest extends ApiTestBase {

    private static final List<String> INGREDIENTS_VALID = List.of(
            "61c0c5a71d1f82001bdaaa6d",
            "61c0c5a71d1f82001bdaaa70"
    );

    private static final List<String> INGREDIENTS_INVALID = List.of("invalid-hash-123");

    private final ObjectMapper mapper = new ObjectMapper();

    private String registerAndLoginUser() {
        String email = "order-" + System.currentTimeMillis() + "@yandex.ru";
        String password = "OrderPass123";
        String name = "Order User";

        userApi.registerUser(email, password, name);
        userApi.loginUser(email, password);

        String token = userApi.getAccessToken();
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalStateException("Access token is missing after login");
        }
        return token;
    }

    @After
    public void cleanup() {
        try {
            userApi.deleteCurrentUser();
        } catch (Exception ignored) {
        }
        userApi.clearTokens();
    }

    @Test
    @Description("Создание заказа: с авторизацией")
    public void testCreateOrderWithAuthAndIngredients() throws Exception {
        String accessToken = registerAndLoginUser();
        var body = Map.of("ingredients", INGREDIENTS_VALID);
        String jsonBody = mapper.writeValueAsString(body);

        given()
                .filter(new AllureRestAssured()) //
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа: без авторизации")
    public void testCreateOrderWithoutAuth() throws Exception {

        var body = Map.of("ingredients", INGREDIENTS_VALID);
        String jsonBody = mapper.writeValueAsString(body);


        Response response = given()
                .filter(new AllureRestAssured())//
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .post("/api/orders");

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа: без ингредиентов")
    public void testCreateOrderWithoutIngredients() throws Exception {
        String accessToken = registerAndLoginUser();
        var body = Map.of("ingredients", List.of());
        String jsonBody = mapper.writeValueAsString(body);

        given()
                .filter(new AllureRestAssured())//
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(400)
                .body("success", is(false));
    }

    @Test
    @Description("Создание заказа: с неверным хешем ингредиентов")
    public void testCreateOrderWithInvalidIngredients() throws Exception {
        String accessToken = registerAndLoginUser();
        var body = Map.of("ingredients", INGREDIENTS_INVALID);
        String jsonBody = mapper.writeValueAsString(body);

        Response response = given()
                .filter(new AllureRestAssured())//
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .post("/api/orders");

        response.then()
                .statusCode(isOneOf(400)) // дает 500, верно 400, тест падает
                .body("success", is(false));
    }
}