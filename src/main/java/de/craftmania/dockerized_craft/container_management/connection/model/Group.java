package de.craftmania.dockerized_craft.container_management.connection.model;

import de.craftmania.dockerized_craft.container_management.connection.Balancer;
import de.craftmania.dockerized_craft.container_management.connection.strategy.Strategy;
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



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(String strategy) {
        if (Balancer.balanceStrategies.containsKey(strategy)) {
            this.strategy = Balancer.balanceStrategies.get(strategy);
        } else {
            this.strategy = Balancer.balanceStrategies.get("balance");
        }
    }

    public Boolean getRestricted() {
        return restricted;
    }

    public void setRestricted(Boolean restricted) {
        this.restricted = restricted;
    }

    public Boolean getCanReconnect() {
        return canReconnect;
    }

    public void setCanReconnect(Boolean canReconnect) {
        this.canReconnect = canReconnect;
    }

    @Override
    public String toString() {
        return super.toString() + "{" + this.name + "," + this.strategy + "}";
    }

    public Map<String, ServerInfo> getServers() {
        return this.servers;
    }

    public void addServer(ServerInfo server) {
        this.servers.put(server.getName(), server);
    }

    public ServerInfo getServer(String name) {
        return this.servers.get(name);
    }


    public void removeServer(ServerInfo server) {
        this.servers.remove(server.getName());
    }


    public void removeServer(String server) {
        this.servers.remove(server);
    }
}
