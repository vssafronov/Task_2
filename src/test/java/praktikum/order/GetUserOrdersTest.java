package praktikum.order;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.api.OrderApiClient;
import praktikum.api.UserApiClient;
import praktikum.user.User;

public class GetUserOrdersTest {
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
        order = Order.randomOrder();
        orderApiClient.createOrder(accessToken, order);
    }

    @Test
    @DisplayName("Проверка получения заказов пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void getUserOrdersSuccessTest() {
        ValidatableResponse getUserOrdersResp = orderApiClient.getUserOrders(accessToken);
        orderApiClient.getOrdersFromList(getUserOrdersResp);
    }

    @Test
    @DisplayName("Проверка получения заказов пользователя без авторизации")
    public void getUserOrdersUnauthorizedErrorTest() {
        ValidatableResponse getUserOrdersResp = orderApiClient.getUserOrders("");
        orderApiClient.getUserOrdersUnauthorized(getUserOrdersResp);
    }

    @AfterEach
    public void tearDown() {
        client.deleteUser(accessToken);
    }
}
