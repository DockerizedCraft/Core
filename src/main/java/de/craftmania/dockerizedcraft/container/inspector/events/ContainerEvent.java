package de.craftmania.dockerizedcraft.container.inspector.events;

import net.md_5.bungee.api.plugin.Event;

import java.net.InetAddress;
import java.util.Map;

public class ContainerEvent extends Event {
    private String id;

    private String action;

    private InetAddress ip;

    private Integer port;

    private String name;

    private Map<String, String> environmentVariables;

    public ContainerEvent(String id, String action) {
        this.id = id;
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(Map<String, String> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }
}
