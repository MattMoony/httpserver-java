import java.net.*;
import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    public String sName = "SERVER";

    private int port;
    private ArrayList<Client> clients = new ArrayList<>();
    private HashMap<String, HashMap<String, String>> paths = new HashMap<>();

    public Server (int port) {
        this.port = port;

        this.paths.put("GET", new HashMap<>());
        this.paths.put("POST", new HashMap<>());
    }
    public Server(int port, String serverName) {
        this(port);
        this.sName = serverName;
    }

    public void start() {
        try ( ServerSocket sSocket = new ServerSocket(this.port) ) {
            System.out.println(" [" + this.sName + "]: Now listening on :" + this.port + " ... ");

            while (true) {
                Client c = new Client(sSocket.accept(), this);
                this.clients.add(c);
                c.start();
            }
        } catch (IOException e) {
            System.out.println(" [ERROR]: Can't listen on :" + this.port);
            System.out.println(e.getMessage());
        }
    }

    public void get(String urlPath, String filePath) {
        this.set("GET", urlPath, filePath);
    }
    public void post(String urlPath, String filePath) {
        this.set("POST", urlPath, filePath);
    }
    public void set(String method, String urlPath, String filePath) {
        this.paths.get(method).put(urlPath, filePath);
    }

    public static void main(String[] args) {
        Server webServer = new Server(80, "HTTP-SERVER");
        webServer.start();
    }
}
