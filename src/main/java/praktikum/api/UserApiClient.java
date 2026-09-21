package praktikum.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.Client;
import praktikum.MsgConfig;
import praktikum.user.Credentials;
import praktikum.user.User;

import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserApiClient {
    private static final String AUTH = "/auth";
    private static final String CREATE = AUTH + "/register";
    private static final String LOGIN = AUTH + "/login";
    private static final String USER = AUTH + "/user";

    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user) {
        return Client.setUp()
                .body(user)
                .when()
                .post(CREATE)
                .then()
                .log().all();
    }

    @Step("Проверка успешного создания пользователя")
    public boolean isUserCreated(ValidatableResponse createResp) {
        boolean isUserCreated = createResp.
                assertThat().statusCode(HttpURLConnection.HTTP_OK)
                .extract()
                .path("success");

        assertTrue(isUserCreated);

        return isUserCreated;
    }

    @Step("Проверка создания уже существующего пользователя")
    public void createUserAlreadyExists(ValidatableResponse createResp) {
        String errorMessage = createResp
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                .extract()
                .path("message");

        assertEquals(MsgConfig.CREATE_USER_ALREADY_EXISTS, errorMessage, "Некорректное сообщение при создании пользователя, который уже зарегистрирован");
    }

    @Step("Проверка создания пользователя без обязательных полей")
    public void createUserWithoutRequiredFields(ValidatableResponse createResp) {
        String errorMessage = createResp
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                .extract()
                .path("message");

        assertEquals(MsgConfig.CREATE_USER_WITHOUT_REQUIRED_FIELDS, errorMessage, "Некорректное сообщение при создании пользователя без обязательных полей");
    }

    @Step("Логин пользователя в системе")
    public ValidatableResponse loginUser(Credentials credentials) {
        return Client.setUp()
                .body(credentials)
                .when()
                .post(LOGIN)
                .then()
                .log().all();
    }

    @Step("Проверка успешного логина пользователя")
    public void loginUserSuccess(ValidatableResponse loginResp) {
        boolean isUserLogin = loginResp
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .extract()
                .path("success");

        assertTrue(isUserLogin);
    }

    @Step("Проверка авторизации пользователя без обязательных полей")
    public void loginUserWithIncorrectFields(ValidatableResponse loginResp) {
        String errorMessage = loginResp
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .extract()
                .path("message");

        assertEquals(MsgConfig.LOGIN_USER_UNAUTHORIZED, errorMessage, "Некорректное сообщение об ошибке при авторизации пользователя");
    }

    @Step("Получение токена")
    public String getAccessToken(ValidatableResponse createResp) {
        return createResp
                .extract()
                .path("accessToken");
    }

    @Step("Изменение данных пользователя")
    public ValidatableResponse changeUserData(String accessToken, User user) {
        return Client.setUp()
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(USER)
                .then()
                .log().all();
    }

    @Step("Проверка корректности изменения данных пользователя")
    public void changeUserDataSuccess(ValidatableResponse changeDataResp) {
        boolean isUserDataChanged = changeDataResp
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .extract()
                .path("success");

        assertTrue(isUserDataChanged);
    }

    @Step("Проверка ошибки при изменении данных пользователя без авторизации")
    public void changeUserDataWithoutAccess(ValidatableResponse changeDataResp) {
        String errorMessage = changeDataResp
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .extract()
                .path("message");

        assertEquals(MsgConfig.CHANGE_USER_DATA_UNAUTHORIZED, errorMessage, "Некорректное сообщение об ошибке при изменении данных пользователя без авторизации");
    }

    @Step("Удаление пользователя")
    public void deleteUser(String accessToken) {
        boolean isDeleted = Client.setUp()
                .header("Authorization", accessToken)
                .when()
                .delete(USER)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_ACCEPTED)
                .extract()
                .path("success");

        assertTrue(isDeleted);
    }
}
