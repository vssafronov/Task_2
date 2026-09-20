package praktikum.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.qameta.allure.Step;
import lombok.*;
import praktikum.api.IngredientApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {
    private String _id;
    private List<String> ingredients;
    private String status;
    private String name;
    private String createdAt;
    private String updatedAt;
    private Integer number;

    @Step("Создание случайного списка ингредиентов для заказа")
    public static Order randomOrder() {
        List<String> ingredientIdList = IngredientApiClient.getIngredientIdList();
        List<String> ingredients = new ArrayList<>();
        int count = 3;
        Random random = new Random();
        if (ingredientIdList.size() < count) count = ingredientIdList.size();
        for (int i = 0; i < count; i++) {
            int element = random.nextInt(ingredientIdList.size());
            ingredients.add(ingredientIdList.get(element));
        }
        Order result = new Order();
        result.setIngredients(ingredients);
        return result;
    }
}
