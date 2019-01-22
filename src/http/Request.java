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
                    extension;
    private HashMap<String, String> headers = new HashMap<>();



    public Request() {}
    public Request(String requestString) {
        this.parseString(requestString);
    }
    public Request(BufferedReader in) {
        this.parseStream(in);
    }



    public void parseString(String requestString) {
        String[] lines = requestString.split("\r\n");

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

        for (int i = 1; i < lines.length; i++) {
            String[] header = lines[i].split(": ");
            this.headers.put(header[0], header[1]);
        }

        this.extension = this.filename.contains(".") ? this.filename.substring(this.filename.lastIndexOf(".")+1) : "";
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
