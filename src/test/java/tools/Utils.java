package tools;

import java.security.SecureRandom;
import java.util.Random;

public class Utils {

    private static final Random RANDOM = new SecureRandom();

    public static String randomString() {
        int length = RANDOM.nextInt(10) + 20;
        char[] chars = new char[length];

        for (int i = 0; i < length; i++) {
            chars[i] = (char) (RANDOM.nextInt(52) + 'A');
        }

        return new String(chars);
    }

}
