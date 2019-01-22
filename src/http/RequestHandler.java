package http;

@FunctionalInterface
public interface RequestHandler<Request, Response> {
    public void apply(Request request, Response response);
}
