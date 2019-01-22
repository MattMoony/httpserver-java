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

    private void handleRequest(String remoteSocket) {
        try {
            Request request = new Request(remoteSocket, this.in, this.mother);
            if (request.type == null)
                return;
            Response response = new Response(request.protocolVersion);
            Document doc;

            // -- REQUEST HANDLING -- //

            if (this.mother.isPath(request.type, request.path)) {
                if (this.mother.methPath(request.type, request.path) instanceof String) {
                    response.setStatus(200, "OK");

                    doc = new Document((String) this.mother.methPath(request.type, request.path));
                    response.body = doc.read();
                } else {
                    ((RequestHandler<Request, Response>) this.mother.methPath(request.type, request.path)).apply(request, response);
                }
            } else {
                response.setStatus(404, "NOT-FOUND");
                response.body = this.mother.errorPage(404);
            }

            // -- RESPONSE HANDLING -- //

            if (response.body instanceof String) {
                response.body = ((String) response.body).getBytes();
            }

            this.outBytes.write(response.headersString().getBytes());
            this.outBytes.write((byte[]) response.body);

            // -- CLOSE THE SOCKET -- //

            this.cSocket.close();
        } catch (IOException e) {
            System.out.println(" [ERROR]: IOException ... ");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println(" [" + this.mother.sName + "]: NEW REQUEST FROM " + this.cSocket.getRemoteSocketAddress().toString().substring(1) + " ... ");
        this.handleRequest(this.cSocket.getRemoteSocketAddress().toString().substring(1));
    }

    public void start() {
        if (this.thread == null) {
            this.thread = new Thread(this);
            this.thread.start();
        }
    }
}
