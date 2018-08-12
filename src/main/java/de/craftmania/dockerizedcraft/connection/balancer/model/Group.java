package de.craftmania.dockerizedcraft.connection.balancer.model;

import de.craftmania.dockerizedcraft.connection.balancer.ConnectionBalancer;
import de.craftmania.dockerizedcraft.connection.balancer.strategy.Strategy;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.HashMap;
import java.util.Map;

public class Group {
    private String name;

    private Strategy strategy;

    private Boolean restricted;

    private Boolean canReconnect;

    private Map<String, ServerInfo> servers;

    public Group(String name, Strategy strategy, Boolean restricted, Boolean canReconnect) {
        this.name = name;
        this.strategy = strategy;
        this.restricted = restricted;
        this.canReconnect = canReconnect;
        this.servers = new HashMap<>();
    }

    public Group(String name, String strategy, Boolean restricted, Boolean canReconnect) {
        this.name = name;
        this.setStrategy(strategy);
        this.restricted = restricted;
        this.canReconnect = canReconnect;
        this.servers = new HashMap<>();
    }


    @SuppressWarnings({"WeakerAccess", "unused"})
    public String getName() {
        return name;
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public Strategy getStrategy() {
        return strategy;
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public void setStrategy(String strategy) {
        if (ConnectionBalancer.balanceStrategies.containsKey(strategy)) {
            this.strategy = ConnectionBalancer.balanceStrategies.get(strategy);
        } else {
            this.strategy = ConnectionBalancer.balanceStrategies.get("balance");
        }
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public Boolean getRestricted() {
        return restricted;
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public void setRestricted(Boolean restricted) {
        this.restricted = restricted;
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public Boolean getCanReconnect() {
        return canReconnect;
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public void setCanReconnect(Boolean canReconnect) {
        this.canReconnect = canReconnect;
    }

    @Override
    public String toString() {
        return super.toString() + "{" + this.name + "," + this.strategy + "}";
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public Map<String, ServerInfo> getServers() {
        return this.servers;
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public void addServer(ServerInfo server) {
        this.servers.put(server.getName(), server);
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public ServerInfo getServer(String name) {
        return this.servers.get(name);
    }


    @SuppressWarnings({"WeakerAccess", "unused"})
    public void removeServer(ServerInfo server) {
        this.servers.remove(server.getName());
    }


    @SuppressWarnings({"WeakerAccess", "unused"})
    public void removeServer(String server) {
        this.servers.remove(server);
    }
}
