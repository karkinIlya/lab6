package anonimizer;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.impl.engine.client.PoolConductor;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.server.Route;
import akka.stream.javadsl.Flow;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static jdk.internal.jline.TerminalFactory.get;

public class Server {

    public static Route createRoute() {
        return Route(get(() ->
                Parametr("url", url ->

                        )

                )
        )
    }

    public static void main(String[] argv) {
        ActorSystem system = ActorSystem.create("routes");
        Http http = Http.get(system);
        ActorRef confActor = system.actorOf(Props.create(ConfActor.class));
        int PORT = Integer.parseInt(argv[0]);

        // init zookeeper

//        final Flow<HttpRequest, HttpResponse, NotUsed> route = createRoute().flow();
        final CompletionStage<> binding = http.bindAndHandle(
                flow,
                connection,
                materializer
                );
        binding.thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }
}
