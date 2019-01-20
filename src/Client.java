import java.net.*;
import java.io.*;

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
        String nLine;

        try {
            while ((nLine = this.in.readLine()) != null) {
                System.out.println(nLine);
            }
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
