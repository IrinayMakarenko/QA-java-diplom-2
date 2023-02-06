package praktikum.generator;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

public class IngredientsGenerator {
    public static List<String> getRandomIngredients() {
        List<String> wrongIngredients = new ArrayList<>();
        wrongIngredients.add(0, RandomStringUtils.randomAlphabetic(24));
        wrongIngredients.add(1, RandomStringUtils.randomAlphabetic(24));
        return wrongIngredients;
    }
}
