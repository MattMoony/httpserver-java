package http;

import web.Documents;

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



    public String getPath(String urlPath) {
        return this.paths.get("GET").get(urlPath);
    }
    public String postPath(String urlPath) {
        return this.paths.get("POST").get(urlPath);
    }
    public String methPath(String method, String urlPath) {
        return this.paths.get(method).get(urlPath);
    }
    public boolean fileExists(String method, String urlPath) {
        return this.methPath(method, urlPath) != null;
    }



    public void mapDirectory(String dirPath) {
        this.mapDirectory("/", dirPath);
    }
    public void mapDirectory(String urlPath, String dirPath) {
        this.mapDirectory(urlPath, dirPath, "GET");
    }
    public void mapDirectory(String urlPath, String dirPath, String method) {
        if (!urlPath.endsWith("/"))
            urlPath+="/";
        if (!dirPath.endsWith("/"))
            dirPath+="/";

        File dir = new File(dirPath);

        for (String p : dir.list()) {
            File cuF = new File(dirPath + p);

            if (cuF.isDirectory()) {
                this.mapDirectory(urlPath+p, dirPath+p, method);
                continue;
            }
            this.set(method,urlPath + p, dirPath + p);
        }

        // set index ...
        for (String f : Documents.indexFiles) {
            File cuF = new File(dirPath + f);

            if (cuF.exists()) {
                this.set(method, urlPath, dirPath+f);
                break;
            }
        }
    }

    public static void main(String[] args) {
        Server webServer = new Server(80, "HTTP-SERVER");
        // webServer.get("/", "html_test/index.php");
        webServer.mapDirectory("html_test/");

        webServer.start();
    }
}
