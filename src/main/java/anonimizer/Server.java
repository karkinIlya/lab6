package anonimizer;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.javadsl.Flow;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import jdk.internal.joptsimple.internal.Strings;

import java.time.Duration;

import static akka.http.javadsl.server.Directives.completeWithFuture;
import static akka.http.javadsl.server.Directives.parameter;
import static jdk.internal.jline.TerminalFactory.get;

public class Server {

    public static final String URL_PARAM = "url";
    public static final String COUNT_PARAM = "count";
    public static final Duration TIMEOUT = Duration.ofSeconds(5);

    public static Route createRoute(Http http, ActorRef confActor) {
        return Route(get(() ->
                        parameter(URL_PARAM, url ->
                                parameter(COUNT_PARAM, count -> {
                                    return Integer.parseInt(count) <= 0 ?
                                            completeWithFuture(http.singleRequest(HttpRequest.create(url))) :
                                            completeWithFuture(Patterns.ask(confActor, new ServerSelector(), TIMEOUT)
                                                    .thenApply(
                                                            port -> (String)port
                                                    ).thenCompose(
                                                            po
                                                    )
                                }
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
