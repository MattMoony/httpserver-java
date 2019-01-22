package web;

public class Documents {
    public static String[] textExtensions = {
        "html", "php", "css", "js", "txt"
    }, indexFiles = {
        "index.html", "index.php", "index.txt"
    };

    public static boolean isTextFile(String extension) {
        for (String ext : textExtensions) {
            if (extension.equalsIgnoreCase(ext))
                return true;
        }
        return false;
    }
}
