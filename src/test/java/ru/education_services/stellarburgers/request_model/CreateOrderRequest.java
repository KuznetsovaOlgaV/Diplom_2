package ru.education_services.stellarburgers.request_model;

import java.util.List;

public class CreateOrderRequest {
    private List<String> ingredients;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(List<String> ingredients) {
        this.ingredients = ingredients != null ? ingredients : List.of();
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}