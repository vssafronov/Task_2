package praktikum.order;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import praktikum.api.OrderApiClient;
import praktikum.api.UserApiClient;
import praktikum.user.User;

import java.util.List;

public class CreateOrderTest {
    private User user;
    private String accessToken;
    private UserApiClient client = new UserApiClient();
    private Order order;
    private OrderApiClient orderApiClient = new OrderApiClient();

    @BeforeEach
    public void setUp() {
        user = User.randomUser();
        ValidatableResponse createResp = client.createUser(user);
        accessToken = client.getAccessToken(createResp);
    }

    @ParameterizedTest(name = "Создание заказа {0}")
    @ValueSource(strings = {"с авторизацией", "без авторизации"})
    @DisplayName("Проверка успешного создания заказа c ингредиентами")
    @Severity(SeverityLevel.CRITICAL)
    public void createOrderWithIngredientsSuccessTest(String value) {
        order = Order.randomOrder();
        String token = accessToken;
        if (!"с авторизацией".equals(value)) token = "";
        ValidatableResponse createOrderResp = orderApiClient.createOrder(token, order);
        orderApiClient.createOrderSuccess(createOrderResp);
    }

    @Test
    @DisplayName("Проверка ошибки при создании заказа без ингредиентов")
    public void createOrderWithoutIngredientsErrorTest() {
        order = new Order();
        order.setIngredients(List.of());
        ValidatableResponse createOrderResp = orderApiClient.createOrder(accessToken, order);
        orderApiClient.createOrderBadRequest(createOrderResp);
    }

    @Test
    @DisplayName("Проверка ошибки при создании заказа с неверным хешем ингредиента")
    public void createOrderWithIncorrectIngredientErrorTest() {
        order = new Order();
        order.setIngredients(List.of(""));
        ValidatableResponse createOrderResp = orderApiClient.createOrder(accessToken, order);
        orderApiClient.createOrderInternalServerError(createOrderResp);
    }

    @AfterEach
    public void tearDown() {
        client.deleteUser(accessToken);
    }
}
