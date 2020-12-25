package anonimizer;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.apache.zookeeper.ZooKeeper;

import static akka.http.javadsl.server.Directives.*;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class Server {

    public static final String URL_PARAM = "url";
    public static final String COUNT_PARAM = "count";
    public static final Duration TIMEOUT = Duration.ofSeconds(5);
    public static final String FORMAT_STRING = "https://%s:%s?url=%s&count=%s";
    public static final String HOST_NAME = "localhost";
    public static final String ZOOKEEPER_PORT = "2181";

    public static Route createRoute(Http http, ActorRef confActor) {
        return route(get(() ->
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
                                                                        String.format(FORMAT_STRING, HOST_NAME,
                                                                                port, url, count)
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
        ZooKeeper zookeeper = new ZooKeeper(HOST_NAME + ":" + ZOOKEEPER_PORT, );

        ActorMaterializer materializer = ActorMaterializer.create(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> flow = createRoute(http, confActor).flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                flow,
                ConnectHttp.toHost(HOST_NAME, PORT)
                materializer
                );
        binding.thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }
}
