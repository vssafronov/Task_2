package praktikum.user;

import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Credentials {
    private String email;
    private String password;

    @Step("Получение кредов пользователя")
    public static Credentials getCredentialsFromUser(User user) {
        return new Credentials(user.getEmail(), user.getPassword());
    }
}
