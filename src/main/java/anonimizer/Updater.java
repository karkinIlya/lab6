package anonimizer;

import java.util.ArrayList;

public class Updater {
    private ArrayList<String> servers;

    public Updater(ArrayList<String> servers) {
        this.servers = servers;
    }

    public Updater() {
        this.servers = new ArrayList<>();
    }

    public ArrayList<String> getServers() {
        return servers;
    }

    public void setServers(ArrayList<String> servers) {
        this.servers = servers;
    }
}
