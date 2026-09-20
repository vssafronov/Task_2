package praktikum.user;

import io.restassured.response.ValidatableResponse;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import praktikum.api.UserApiClient;

import java.util.stream.Stream;

public class ChangeUserDataTest {
    private User user;
    private UserApiClient client = new UserApiClient();
    private String accessToken;

    @BeforeEach
    public void setUp() {
        user = User.randomUser();
        ValidatableResponse createResp = client.createUser(user);
        accessToken = client.getAccessToken(createResp);
    }

    @ParameterizedTest(name = "Изменение параметра {0} на значение {1}")
    @MethodSource("userDataCases")
    @DisplayName("Проверка изменения данных пользователя")
    public void changeUserDataSuccessTest(String field, String value) {
        User newUser = new User();
        if ("email".equals(field)) newUser.setEmail(value);
        if ("password".equals(field)) newUser.setPassword(value);
        if ("name".equals(field)) newUser.setName(value);
        ValidatableResponse changeDataResp = client.changeUserData(accessToken, newUser);
        client.changeUserDataSuccess(changeDataResp);
    }

    @ParameterizedTest(name = "Изменение параметра {0} на значение {1}")
    @MethodSource("userDataCases")
    @DisplayName("Проверка изменения данных пользователя без авторизации")
    public void changeUserDataWithoutAccessErrorTest(String field, String value) {
        User newUser = new User();
        if ("email".equals(field)) newUser.setEmail(value);
        if ("password".equals(field)) newUser.setPassword(value);
        if ("name".equals(field)) newUser.setName(value);
        ValidatableResponse changeDataResp = client.changeUserData("Bearer", newUser);
        client.changeUserDataWithoutAccess(changeDataResp);
    }

    private static Stream<Arguments> userDataCases() {
        Faker f = new Faker();
        String email = f.bothify("?????????#####@ya.ru");

        return Stream.of(
                Arguments.of("email", email),
                Arguments.of("password", "321"),
                Arguments.of("name", "Name")
        );
    }

    @AfterEach
    public void tearDown() {
        client.deleteUser(accessToken);
    }
}
