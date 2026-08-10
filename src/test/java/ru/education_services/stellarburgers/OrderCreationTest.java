package ru.education_services.stellarburgers;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import ru.education_services.stellarburgers.api.ApiTestBase;

import java.util.List;
import java.util.UUID;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class OrderCreationTest extends ApiTestBase {

    private static final List<String> INGREDIENTS_VALID = List.of(
            "61c0c5a71d1f82001bdaaa6d",
            "61c0c5a71d1f82001bdaaa70"
    );
    private static final List<String> INGREDIENTS_INVALID = List.of("invalid-hash-123");

    private String accessToken;

    @Before
    public void setUp() {
        String email = "order-" + UUID.randomUUID() + "@yandex.ru";
        String password = "OrderPass123";
        String name = "Order User";

        userApi.registerUser(email, password, name);
        userApi.loginUser(email, password);
        accessToken = userApi.getAccessToken();

        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalStateException("Access token must not be empty before test");
        }
    }

    @Test
    @Description("Создание заказа: с авторизацией и валидными ингредиентами")
    public void testCreateOrderWithAuthAndIngredients() {
        Response response = orderApi.createOrder(accessToken, INGREDIENTS_VALID);
        response.then()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа: без авторизации (валидные ингредиенты)")
    public void testCreateOrderWithoutAuth() {
        Response response = orderApi.createOrder(null, INGREDIENTS_VALID);
        response.then()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа: без ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        Response response = orderApi.createOrder(accessToken, List.of());
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Description("Создание заказа: с неверным хешем ингредиентов") // дает 500, верно 400, тест падает
    public void testCreateOrderWithInvalidIngredients() {
        Response response = orderApi.createOrder(accessToken, INGREDIENTS_INVALID);
        response.then()
                .statusCode(SC_BAD_REQUEST)   // дает 500, верно 400, тест падает
                .body("success", is(false))
                .body("message", equalTo("One or more ids are invalid"));
    }
}