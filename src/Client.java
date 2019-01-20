import java.net.*;
import java.io.*;
import java.util.stream.Stream;

public class Client implements Runnable {
    private Thread thread;

    private Socket cSocket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(Socket cSocket) throws IOException {
        this.cSocket = cSocket;
        this.out = new PrintWriter(cSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
    }

    @Override
    public void run() {
        String request = "";

        try {
            char[] buff = new char[1];
            this.in.read(buff);

            System.out.println("now");
            this.out.println("HTTP/1.1 200 OK\r\nContent-type: text/html; charset=\"utf-8\"\r\n\r\n<!DOCTYPE html><html><head></head><body><h1>Hello World!</h1></body></html>");
        } catch (IOException e) {
            System.out.println(" [ERROR]: IOException ... ");
            System.out.println(e.getMessage());
        }

        System.out.println(" [+] Done!");
    }

    public void start() {
        if (this.thread == null) {
            this.thread = new Thread(this);
            this.thread.start();
        }
    }
}
