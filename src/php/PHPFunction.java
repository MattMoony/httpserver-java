package php;

import java.util.HashMap;

public class PHPFunction {
    private String body;
    private HashMap<String, String> arguments;

    public PHPFunction(HashMap<String, String> args, String body) {
        this.arguments = new HashMap<>(args);
        this.body = body;
    }

    public Object exec(String[] values) {
        return new Object();
    }
}
