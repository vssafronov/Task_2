package praktikum.api;

import io.qameta.allure.Step;
import praktikum.Client;

import java.util.List;

public class IngredientApiClient {
    private static final String INGREDIENTS = "/ingredients";

    @Step("Получение всех id ингредиентов")
    public static List<String> getIngredientIdList() {
        return Client.setUp()
                .get(INGREDIENTS)
                .then()
                .log().status()
                .extract()
                .path("data._id");
    }
}
