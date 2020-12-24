package anonimizer;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;

public class ConfActor extends AbstractActor {
    ArrayList<String> servers = new ArrayList<String>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(
                        Updater.class, message -> {
                            servers.add
                        }
                )
                .match(

                )
    }
}
