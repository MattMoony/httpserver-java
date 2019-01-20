import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Server {
    private int port;
    private ArrayList<Client> clients = new ArrayList<>();

    public Server (int port) {
        this.port = port;
    }

    public void start() {
        try ( ServerSocket sSocket = new ServerSocket(this.port) ) {
            System.out.println(" [SERVER]: Now listening on :" + this.port + " ... ");

            while (true) {
                Client c = new Client(sSocket.accept());
                this.clients.add(c);
                c.start();
            }
        } catch (IOException e) {
            System.out.println(" [ERROR]: Can't listen on :" + this.port);
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server webServer = new Server(80);
        webServer.start();
    }
}
