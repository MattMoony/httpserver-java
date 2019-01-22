package web;

public class URL {
    public static String decode(String url) {
        if (url.indexOf('%') < 0)
            return url;

        int p = url.indexOf('%');
        String str = url.substring(0, p);

        while (++p < url.length()) {
            str += (char) Integer.parseInt(url.substring(p, p+2), 16);
            p++;

            while (++p < url.length() && url.charAt(p) != '%')
                str += url.charAt(p);
        }

        return str;
    }

    public static String encode (String str) {
        char[] repl = {'?', '#', '[', ']', '@', '!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=', '%', '{', '}'};

        for (char r : repl)
            str = str.replace(r+"", "%" + Integer.toString((int)r, 16).toUpperCase());
        return str;
    }
}
