package http;

import web.URL;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Request {
    public String protocolVersion,
                    type,
                    urlPath,
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
    public Request(String remoteSocket, InputStream in, Server server) {
        this(remoteSocket, server);
        this.parseStream(in);
    }



    public void parseString(String requestString) {
        // -- GET LINES -- //
        String[] lines = requestString.split("\r\n");
        if (!lines[0].contains(" "))
            return;

        // -- REQUEST: FIRST LINE -- //
        this.type = lines[0].split(" ")[0].toUpperCase();
        this.urlPath = URL.decode(lines[0].split(" ")[1]);
        if (this.urlPath.contains("?"))
            this.path = this.urlPath.split("[?]")[0];
        else
            this.path = this.urlPath;

        this.protocolVersion = lines[0].split(" ")[2];

        // -- GET PARAMETERS -- //
        if (this.urlPath.contains("?")) {
            this.parseGetParameters(this.urlPath);
        }

        // -- DIRECTORY PATH -- //
        String[] pathParts = this.path.split("/");
        this.directory = "";
        for (int i = 0; i < pathParts.length-1; i++) {
            this.directory += pathParts[i] + "/";
        }

        // -- FILENAME -- //
        if (pathParts.length > 0)
            this.filename = pathParts[pathParts.length-1];
        else
            this.filename = "/";

        // -- HEADERS -- //
        int i;
        for (i = 1; i < lines.length; i++) {
            if (lines[i].equals(""))
                break;
            String[] header = lines[i].split(": ");
            this.headers.put(header[0], header[1]);
        }

        // -- POST PARAMETERS -- //
        if (++i<lines.length) {
            String[] paramParts = new String[lines.length-i];
            for (int j = 0; i < lines.length; i++, j++)
                paramParts[j] = lines[i];

            this.parsePostParameters(paramParts);
        }

        // -- FILE EXTENSION -- //
        this.extension = this.filename.contains(".") ? this.filename.substring(this.filename.lastIndexOf(".")+1) : "";

        // -- URL PARAMETERS -- //
        this.patternPath = this.server.getPatternPath(this.type, this.path);
        if (this.patternPath!=null)
            this.parseURLParameters(this.path);
    }
    public void parseStream(InputStream inStream) {
        // BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
        String requestString = "";

        try {
            while (!(inStream.available() > 0)) {}
            int c;
            while (inStream.available() > 0 && (c = inStream.read()) != -1 && c != '\0') {
                requestString += (char) c;
            }
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
        this.parseGetParameters(this.urlPath);
    }
    public void parseGetParameters(String path) {
        String[] params = path.split("[?]")[1].split("&");
        for (String p : params) {
            if (p.contains("=")) {
                String[] parts = p.split("=");
                this.getParams.put(parts[0].trim(), parts.length > 1 ? parts[1].trim() : "");
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
                            this.postParams.put(arg.split("=")[0].trim(), arg.split("=").length > 1 ?
                                    arg.split("=")[1].trim() : "");
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
