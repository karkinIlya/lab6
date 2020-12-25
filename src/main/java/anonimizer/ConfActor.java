package anonimizer;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.Random;

public class ConfActor extends AbstractActor {
    private ArrayList<String> servers;
    private Random r = new Random(123);

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(
                        Updater.class, message -> {
                            servers = message.getServers();
                        }
                )
                .match(
                        ServerSelector.class, message -> {
                            getSender().tell(
                                    servers.get(r.nextInt(servers.size())),
                                    ActorRef.noSender()
                            );
                        }
                ).build();
    }
}
