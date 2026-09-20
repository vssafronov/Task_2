package praktikum.user;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import praktikum.api.UserApiClient;

import java.util.stream.Stream;

public class LoginUserTest {
    private User user;
    private Credentials credentials;
    private UserApiClient client = new UserApiClient();
    private ValidatableResponse createResp;

    @BeforeEach
    public void setUp() {
        user = User.randomUser();
        createResp = client.createUser(user);
        credentials = Credentials.getCredentialsFromUser(user);
    }

    @Test
    @DisplayName("Проверка успешной авторизации пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void loginUserSuccessTest() {
        ValidatableResponse loginResp = client.loginUser(credentials);
        client.loginUserSuccess(loginResp);
    }

    @ParameterizedTest(name = "Проверка авторизации с {0} = {1}")
    @MethodSource("userLoginCases")
    @DisplayName("Проверка авторизации с неверным логином и паролем")
    public void loginUserUnauthorizedErrorTest(String field, String value) {
        if ("email".equals(field)) credentials.setEmail(value);
        if ("password".equals(field)) credentials.setPassword(value);

        ValidatableResponse loginResp = client.loginUser(credentials);
        client.loginUserWithIncorrectFields(loginResp);
    }

    private static Stream<Arguments> userLoginCases() {
        return Stream.of(
                Arguments.of("email", "1"),
                Arguments.of("password", "1"),
                Arguments.of("email", null),
                Arguments.of("password", null)
        );
    }

    @AfterEach
    public void tearDown() {
        String accessToken = client.getAccessToken(createResp);
        client.deleteUser(accessToken);
    }
}
