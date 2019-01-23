package php;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PHPParser {
    private HashMap<String, PHPFunction> functions = new HashMap<>();
    private HashMap<String, String> variables = new HashMap<>();


    public String parseCode(Matcher matcher) {
        String output = "";

        while (matcher.find()) {
            String line = matcher.group().trim();

            if (line.startsWith("$")) {
                // -- VARIABLE ACTION -- //
            } else if (line.startsWith("function")) {
                // -- FUNCTION DECLARATION -- //
            } else if (line.startsWith("class")) {
                // -- CLASS DECLARATION -- //
            } else{
                // -- FUNCTION CALL -- //
                String[] callParts = line.split("(?:\\(|\\))(?=(?:[^\"']*(?:\"|')[^\"']*(?:\"|'))*[^\"|']*$)");

                callParts[0] = callParts[0].trim();
                callParts[1] = callParts[1].trim();

                String[] args = callParts[1].split(",(?=(?:[^\"']*(?:\"|')[^\"']*(?:\"|'))*[^\"|']*$)");

                if (this.functions.containsKey(callParts[0])) {

                } else {
                    switch (callParts[0]) {
                        case "echo": {

                            }
                            break;
                        case "define": {

                            }
                            break;
                        case "array": {

                            }
                            break;
                    }
                }

            }
        }

        return output;
    }

















    public static String typeof(String value) {
        value = value.trim();

        if ((value.startsWith("'") && value.endsWith("'")) || (value.startsWith("\"") && value.endsWith("\""))) {
            return "string";
        } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return "boolean";
        } else if (value.matches("[\\d]+")) {
            return "integer";
        } else if (value.matches("[\\d.]+")) {
            return "float";
        } else if (value.equalsIgnoreCase("null")) {
            return "null";
        } else if (value.startsWith("array") || value.startsWith("[")) {
            return "array";
        } else if (value.startsWith("new")) {
            String classPart = value.split(" ")[1].trim();
            return "object [" + (classPart.contains("(") ? classPart.substring(0, classPart.indexOf("(")) : classPart) + "]";
        } else {
            return "object?!";
        }
    }


    public void setVariables(HashMap<String, String> variables) {
        this.variables = variables;
    }
}
