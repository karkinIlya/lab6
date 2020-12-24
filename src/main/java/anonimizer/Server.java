package anonimizer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.Http;

import java.net.http.HttpRequest;

public class Server {
    public static void main(String[] argv) {
        ActorSystem system = ActorSystem.create("routes");
        Http http = Http.get(system);
        ActorRef confActor = system.actorOf(Props.create(ConfActor.class));
        int PORT = Integer.parseInt(argv[0]);

        final Flow<HttpRequest, HttpResponce> route = createRoute().flow();

    }
}
