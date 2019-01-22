package http;

import web.URL;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class Request {
    public String protocolVersion,
                    type,
                    path,
                    directory,
                    filename,
                    extension,
                    remoteIp,
                    remotePort,
                    remoteSocket,
                    patternPath;
    private Server server;
    private HashMap<String, String> headers = new HashMap<>();

    public HashMap<String, String> urlParams = new HashMap<>(),
                                    getParams = new HashMap<>(),
                                    postParams = new HashMap<>();

    public Request(String remoteSocket, Server server) {
        this.remoteSocket = remoteSocket;
        this.remoteIp = this.remoteSocket.split(":")[0];
        this.remotePort = this.remoteSocket.split(":")[1];

        this.server = server;
    }
    public Request(String remoteSocket, String requestString, Server server) {
        this(remoteSocket, server);
        this.parseString(requestString);
    }
    public Request(String remoteSocket, BufferedReader in, Server server) {
        this(remoteSocket, server);
        this.parseStream(in);
    }



    public void parseString(String requestString) {
        String[] lines = requestString.split("\r\n");
        if (!lines[0].contains(" "))
            return;

        this.type = lines[0].split(" ")[0].toUpperCase();
        this.path = URL.decode(lines[0].split(" ")[1]);
        this.protocolVersion = lines[0].split(" ")[2];

        String[] pathParts = this.path.split("/");
        this.directory = "";
        for (int i = 0; i < pathParts.length-1; i++) {
            this.directory += pathParts[i] + "/";
        }
        if (pathParts.length > 0)
            this.filename = pathParts[pathParts.length-1];
        else
            this.filename = "/";

        int i;
        for (i = 1; i < lines.length; i++) {
            if (lines[i].equals(""))
                break;
            String[] header = lines[i].split(": ");
            this.headers.put(header[0], header[1]);
        }

        if (++i<lines.length) {
            String[] paramParts = new String[lines.length-i];
            for (; i < lines.length; i++)
                paramParts[i] = lines[i];

            this.parsePostParameters(paramParts);
        }

        this.extension = this.filename.contains(".") ? this.filename.substring(this.filename.lastIndexOf(".")+1) : "";

        if (this.path.contains("?"))
            this.parseGetParameters(this.path);

        this.patternPath = this.server.getPatternPath(this.type, this.path);
        if (this.patternPath!=null)
            this.parseURLParameters(this.path);
    }
    public void parseStream(BufferedReader in) {
        String requestString = "";

        try {
            String line;

            while ((line = in.readLine()) != null && line.length() > 0)
                requestString += line + "\r\n";
        } catch (IOException e) {
            System.out.println(" [ERROR]: IOException whilst parsing stream ... ");
            System.out.println(e.getMessage());
        }

        this.parseString(requestString);
    }

    public void parseURLParameters() {
        this.parseURLParameters(this.path);
    }
    public void parseURLParameters(String path) {
        String[] patternParts = this.patternPath.split("/"),
                pathParts = path.split("/");

        for (int i = 0; i < patternParts.length; i++) {
            if (patternParts[i].matches("\\s*") || pathParts[i].matches("\\s*"))
                continue;

            if (patternParts[i].matches("<[^/.]+>")) {
                this.urlParams.put(patternParts[i].substring(1, patternParts[i].length()-1), pathParts[i]);
            }
        }
    }

    public void parseGetParameters() {
        this.parseGetParameters(this.path);
    }
    public void parseGetParameters(String path) {
        String[] params = path.split("[?]")[1].split("&");
        for (String p : params) {
            if (p.contains("=")) {
                String[] parts = p.split("=");
                this.getParams.put(parts[0].trim(), parts[1].trim());
            } else {
                this.getParams.put(p.trim(), "");
            }
        }
    }

    public void parsePostParameters(String[] paramParts) {
        switch (this.getHeader("Content-Type")) {
            case "application/x-www-form-urlencoded": {
                for (String pPart : paramParts) {
                    String[] args = pPart.split("&");
                    for (String arg : args) {
                        if (arg.contains("=")) {
                            this.postParams.put(arg.split("=")[0].trim(), arg.split("=")[1].trim());
                        } else {
                            this.postParams.put(arg.trim(), "");
                        }
                    }
                }
            }
            break;
            case "multipart/form-data": {

            }
            break;
        }
    }



    public HashMap<String, String> getHeaders() {
        return this.headers;
    }
    public String getHeader(String header) {
        return this.headers.get(header);
    }

    public void setHeader(String header, String value) {
        this.headers.put(header, value);
    }



    @Override
    public String toString() {
        String ret = this.type + " " + this.path + " " + this.protocolVersion;

        for (String key : this.headers.keySet()) {
            ret += "\r\n" + key + ": " + this.headers.get(key);
        }

        return ret;
    }
}
