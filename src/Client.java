import java.net.*;
import java.io.*;
import java.util.stream.Stream;

public class Client implements Runnable {
    private Thread thread;
    private Server mother;

    private Socket cSocket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(Socket cSocket, Server mom) throws IOException {
        this.cSocket = cSocket;
        this.out = new PrintWriter(cSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
        this.mother = mom;
    }

    private String urlDecode(String url) {
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

    private String urlEncode (String str) {
        char[] repl = {'?', '#', '[', ']', '@', '!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=', '%', '{', '}'};

        for (char r : repl)
            str = str.replace(r+"", "%" + Integer.toString((int)r, 16).toUpperCase());
        return str;
    }

    private void handleRequest() {
        try {
            String[] fHead = this.in.readLine().split(" ");

            this.out.println("HTTP/1.1 200 OK\r\nContent-type: text/html; charset=\"utf-8\"\r\n\r\n<!DOCTYPE html><html><head></head><body><h1>Hello World!</h1></body></html>");
        } catch (IOException e) {
            System.out.println(" [ERROR]: IOException ... ");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println(" [" + this.mother.sName + "]: NEW REQUEST FROM " + this.cSocket.getInetAddress().toString().substring(1) + " ... ");
        this.handleRequest();
    }

    public void start() {
        if (this.thread == null) {
            this.thread = new Thread(this);
            this.thread.start();
        }
    }
}
