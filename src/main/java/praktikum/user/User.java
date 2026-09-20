package praktikum.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.qameta.allure.Step;
import lombok.*;
import net.datafaker.Faker;

import java.util.Locale;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private String email;
    private String password;
    private String name;

    @Step("Создание случайных кредов для нового пользователя")
    public static User randomUser() {
        Faker f = new Faker(new Locale("ru"));
        String email = f.bothify("???????###@ya.ru");
        String password = f.bothify("???????###!");
        String name = f.name().firstName();
        return new User(email, password, name);
    }
}
