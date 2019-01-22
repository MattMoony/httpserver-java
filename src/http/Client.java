package http;

import web.Document;

import java.net.*;
import java.io.*;

public class Client implements Runnable {
    private Thread thread;
    private Server mother;

    private Socket cSocket;
    private PrintWriter out;
    private OutputStream outBytes;
    private BufferedReader in;

    public Client(Socket cSocket, Server mom) throws IOException {
        this.cSocket = cSocket;
        this.outBytes = cSocket.getOutputStream();
        this.out = new PrintWriter(this.outBytes, true);
        this.in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
        this.mother = mom;
    }

    private void handleRequest() {
        try {
            Request request = new Request(this.in);
            Response response = new Response(request.protocolVersion);
            Document doc;

            if (this.mother.fileExists(request.type, request.path)) {
                response.setStatus(200, "OK");

                doc = new Document(this.mother.methPath(request.type, request.path));
                response.body = doc.read();
            } else {
                response.setStatus(404, "NOT-FOUND");
                response.body = "<!DOCTYPE html><html><head></head><body><h1>Error 404: Page not found!</h1></body></html>";

                this.out.println(response);
                this.cSocket.close();

                return;
            }

            if (doc.isText()) {
                this.out.println(response);
            } else {
                this.outBytes.write(response.headersString().getBytes());
                this.outBytes.write((byte[]) response.body);
            }

            this.cSocket.close();
        } catch (IOException e) {
            System.out.println(" [ERROR]: IOException ... ");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println(" [" + this.mother.sName + "]: NEW REQUEST FROM " + this.cSocket.getInetAddress().toString().substring(1) + " ... ");
        this.handleRequest();
    }

    public void start() {
        if (this.thread == null) {
            this.thread = new Thread(this);
            this.thread.start();
        }
    }
}
