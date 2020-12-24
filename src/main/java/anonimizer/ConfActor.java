package anonimizer;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;

public class ConfActor extends AbstractActor {
    private ArrayList<String> servers;

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
                            getSender().tell();
                        }
                ).build();
    }
}
