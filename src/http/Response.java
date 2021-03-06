package http;

import web.Document;

import java.io.FileNotFoundException;
import java.util.HashMap;

public class Response {
    private int statusCode;
    private String statusShort,
                    protocolVersion;
    private HashMap<String, String> headers = new HashMap<>();

    public Object body = "";



    public Response() {}
    public Response(int code) {
        this.statusCode = code;
    }
    public Response(int code, String statusShort) {
        this(code);
        this.statusShort = statusShort;
    }
    public Response(int code, String statusShort, String protocolVersion) {
        this(code, statusShort);
        this.protocolVersion = protocolVersion;
    }
    public Response(int code, String statusShort, String protocolVersion, String body) {
        this(code, statusShort, protocolVersion);
        this.body = body;
    }
    public Response(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }
    public Response(String protocolVersion, String body) {
        this(protocolVersion);
        this.body = body;
    }



    public void setStatus(int code) {
        this.setStatus(code, "-");
    }
    public void setStatus(int code, String shortDescription) {
        this.statusCode = code;
        this.statusShort = shortDescription;

        if (this.body.equals(""))
            this.body = this.statusCode + ": " + this.statusShort;
    }
    public void setHeader(String header, String value) {
        this.headers.put(header, value);
    }



    public int getStatusCode() {
        return this.statusCode;
    }
    public String getHeader(String header) {
        return this.headers.get(header);
    }



    public String headersString() {
        String ret = this.protocolVersion + " " + this.statusCode + " " + this.statusShort + "\r\n";

        for (String key : this.headers.keySet()) {
            ret += key + ": " + this.headers.get(key) + "\r\n";
        }

        return ret + "\r\n";
    }
    public void send(String body) {
        this.body = body;
    }
    public void sendFile(String filePath) throws FileNotFoundException {
        this.body = Document.readDocument(filePath);
    }


    @Override
    public String toString() {
        String ret = this.protocolVersion + " " + this.statusCode + " " + this.statusShort + "\r\n";

        for (String key : this.headers.keySet()) {
            ret += key + ": " + this.headers.get(key) + "\r\n";
        }

        ret += "\r\n" + this.body;
        return ret;
    }
}
