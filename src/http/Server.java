package http;

import web.Document;
import web.Documents;

import java.net.*;
import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    public String sName = "SERVER";

    private int port;
    private ArrayList<Client> clients = new ArrayList<>();
    private HashMap<String, HashMap<String, Object>> paths = new HashMap<>();
    private HashMap<Integer, String> error_pages = new HashMap<>();

    {
        try {
            this.error_pages.put(400, (String) Document.readDocument("src/default/400.html"));
            this.error_pages.put(401, (String) Document.readDocument("src/default/401.html"));
            this.error_pages.put(403, (String) Document.readDocument("src/default/403.html"));
            this.error_pages.put(404, (String) Document.readDocument("src/default/404.html"));
        } catch (FileNotFoundException e) {}
    }

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
    public void get(String urlPath, RequestHandler<Request, Response> callback) {
        this.set("GET", urlPath, callback);
    }
    public void post(String urlPath, String filePath) {
        this.set("POST", urlPath, filePath);
    }
    public void post(String urlPath, RequestHandler<Request, Response> callback) {
        this.set("POST", urlPath, callback);
    }
    public void set(String method, String urlPath, String filePath) {
        this.paths.get(method).put(urlPath, filePath);
    }
    public void set(String method, String urlPath, RequestHandler<Request, Response> callback) {
        this.paths.get(method).put(urlPath, callback);
    }



    public Object getPath(String urlPath) {
        return this.paths.get("GET").get(urlPath);
    }
    public Object postPath(String urlPath) {
        return this.paths.get("POST").get(urlPath);
    }
    public Object methPath(String method, String urlPath) {
        return this.paths.get(method).get(this.getPatternPath(method, urlPath));
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
    public String errorPage(int code) {
        if (this.error_pages.keySet().contains(code))
            return this.error_pages.get(code);
        return "";
    }
    public boolean isPath(String method, String path) {
        return this.methPath(method, path) != null;
    }
    public String getPatternPath(String method, String path) {
        method = method.toUpperCase();

        for (String p : this.paths.get(method).keySet()) {
            String regex = p.replaceAll("<[^/.]+>", ".+");
            if (path.matches(regex + (regex.endsWith("/") ? "?" : "/?")))
                return p;
        }

        return null;
    }



    public static void main(String[] args) {
        Server webServer = new Server(80, "HTTP-SERVER");

        webServer.mapDirectory("html_test/");
        webServer.mapDirectory("/the-same/", "html_test/");



        webServer.get("/special", (request, response) -> {
            response.body = "It worked!";
            System.out.println(" [SPECIAL-HANDLER]: " + request.remoteIp +
                    ":" + request.remotePort + " -> Requested /special ... ");
        });
        webServer.get("/video", (request, response) -> {
            try {
                response.sendFile("html_test/video.mp4");
            } catch (FileNotFoundException e) {}
        });
        webServer.get("/special/<code>/", (req, res) -> {
            System.out.println(" [/special/<code>/] params: " + req.urlParams);
        });
        webServer.get("/something/<a>/<b>/<c>/", (req, res) -> {
            System.out.println(" [/something/<a>/<b>/<c>/] params: " + req.urlParams);
        });



        webServer.start();
    }
}
