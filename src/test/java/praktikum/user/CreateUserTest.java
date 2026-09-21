package praktikum.user;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import praktikum.api.UserApiClient;

public class CreateUserTest {
    private User user;
    private UserApiClient client = new UserApiClient();
    private boolean isUserCreated = false;
    private ValidatableResponse createResp;

    @BeforeEach
    public void setUp() {
        user = User.randomUser();
    }

    @Test
    @DisplayName("Проверка успешного создания пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void createUserSuccessTest() {
        createResp = client.createUser(user);
        isUserCreated = client.isUserCreated(createResp);
    }

    @Test
    @DisplayName("Проверка создания уже существующего пользователя")
    public void createExistsUserErrorTest() {
        createResp = client.createUser(user);
        isUserCreated = client.isUserCreated(createResp);
        ValidatableResponse createRespError = client.createUser(user);
        client.createUserAlreadyExists(createRespError);
    }

    @ParameterizedTest(name = "Проверка ошибки при создании пользователя без обязательного поля {0}")
    @ValueSource(strings = {"email", "password", "name"})
    @DisplayName("Проверка ошибки при создании пользователя без обязательного поля")
    public void createUserWithoutRequiredFieldErrorTest(String field) {
        if ("email".equals(field)) user.setEmail(null);
        if ("password".equals(field)) user.setPassword(null);
        if ("name".equals(field)) user.setName(null);

        createResp = client.createUser(user);
        client.createUserWithoutRequiredFields(createResp);
    }

    @AfterEach
    public void tearDown() {
        if (isUserCreated) {
            String accessToken = client.getAccessToken(createResp);
            client.deleteUser(accessToken);
        }
    }
}
