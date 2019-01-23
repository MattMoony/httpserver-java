package php;

import java.util.HashMap;
import java.util.function.Function;

public class PHP {
    public static String[] call(String function, String... params) {
        // -- ret --
        // ret[0] -> output stream
        // ret[1] -> return value
        String[] ret = new String[]{null, null};

        switch (function) {
            case "echo":
                ret[0] = String.join("", params);
                break;
            case "print":
                ret[0] = String.join("", params);
                break;
            case "define":

                break;
            case "array":

                break;
        }

        return ret;
    }
}
