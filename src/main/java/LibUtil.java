public class LibUtil {
    public static String convertCamelToSnake(String camelCase) {
        return camelCase
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }
}
