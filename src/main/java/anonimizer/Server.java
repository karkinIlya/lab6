package anonimizer;



public class Server {

    public static final String URL_PARAM = "url";
    public static final String COUNT_PARAM = "count";
    public static final Duration TIMEOUT = Duration.ofSeconds(5);
    public static final String FORTAM_STRING = "https://localhost:%s?url=%s&count=%s";

    public static Route createRoute(Http http, ActorRef confActor) {
        return Route(get(() ->
                        parameter(URL_PARAM, url ->
                                parameter(COUNT_PARAM, count ->
                                        Integer.parseInt(count) <= 0 ?
                                                (completeWithFuture(http.singleRequest(HttpRequest.create(url)))) :
                                                (completeWithFuture(Patterns.ask(confActor, new ServerSelector(),
                                                        TIMEOUT)
                                                        .thenApply(
                                                                port -> (String)port
                                                        ).thenCompose(
                                                                port -> http.singleRequest(HttpRequest.create(
                                                                        String.format(FORTAM_STRING, port, url, count)
                                                                        )
                                                                )
                                                        )
                                                ))
                                )
                        )

                )
        );
    }

    public static void main(String[] argv) {
        ActorSystem system = ActorSystem.create("routes");
        Http http = Http.get(system);
        ActorRef confActor = system.actorOf(Props.create(ConfActor.class));
        int PORT = Integer.parseInt(argv[0]);

        // init zookeeper

        final Flow<HttpRequest, HttpResponse, NotUsed> route = createRoute(http, confActor).flow();
        final CompletionStage<> binding = http.bindAndHandle(
                flow,
                connection,
                materializer
                );
        binding.thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }
}
