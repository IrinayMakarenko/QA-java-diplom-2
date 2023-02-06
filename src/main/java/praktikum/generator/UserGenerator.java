package praktikum.generator;

import org.apache.commons.lang3.RandomStringUtils;
import praktikum.pojo.UserRequest;

public class UserGenerator {
    public static UserRequest getRandomUserRequest() {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(RandomStringUtils.randomAlphabetic(12) + "@yandex.ru");
        userRequest.setPassword(RandomStringUtils.randomAlphabetic(10));
        userRequest.setName("Irina");
        return userRequest;
    }
}
