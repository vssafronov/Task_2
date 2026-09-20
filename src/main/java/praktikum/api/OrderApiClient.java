package praktikum.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.Client;
import praktikum.MsgConfig;
import praktikum.order.Order;

import java.net.HttpURLConnection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderApiClient {
    private static final String ORDERS = "/orders";

    @Step("Создание заказа")
    public ValidatableResponse createOrder(String accessToken, Order order) {
        return Client.setUp()
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ORDERS)
                .then()
                .log().all();
    }

    @Step("Проверка успешного создания заказа")
    public void createOrderSuccess(ValidatableResponse createOrderResp) {
        boolean isOrderCreated = createOrderResp
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .extract()
                .path("success");

        assertTrue(isOrderCreated);
    }

    @Step("Проверка создания заказа без ингредиентов")
    public void createOrderBadRequest(ValidatableResponse createOrderResp) {
        String errorMessage = createOrderResp
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .extract()
                .path("message");

        assertEquals(MsgConfig.CREATE_ORDER_BAD_REQUEST, errorMessage, "Некорректное сообщение при создании заказа без ингредиентов");
    }

    @Step("Проверка создания заказа с неверным хешем ингредиентов")
    public void createOrderInternalServerError(ValidatableResponse createOrderResp) {
        createOrderResp
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    @Step("Получение заказов пользователя")
    public ValidatableResponse getUserOrders(String accessToken) {
        return Client.setUp()
                .header("Authorization", accessToken)
                .when()
                .get(ORDERS)
                .then()
                .log().all();
    }

    @Step("Проверка наличия списка заказов")
    public List<Order> getOrdersFromList(ValidatableResponse createResp) {
        List<Order> orderList = createResp
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .extract()
                .body()
                .jsonPath()
                .getList("orders", Order.class);

        assertFalse(orderList.isEmpty(), "Список заказов не должен быть пустой");

        return orderList;
    }

    @Step("Проверка получения заказов пользователя без авторизации")
    public void getUserOrdersUnauthorized(ValidatableResponse createOrderResp) {
        String errorMessage = createOrderResp
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .extract()
                .path("message");

        assertEquals(MsgConfig.CREATE_ORDER_UNAUTHORIZED, errorMessage, "Некорректное сообщение при получении заказов пользователя без авторизации");
    }
}
