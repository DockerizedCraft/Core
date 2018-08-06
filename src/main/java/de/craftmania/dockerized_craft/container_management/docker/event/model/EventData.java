package de.craftmania.dockerized_craft.container_management.docker.event.model;

import java.net.InetAddress;
import java.util.Map;

public class EventData {
    private String id;

    private String action;

    private InetAddress ip;

    private Integer port;

    private String name;

    private Map<String, String> environmentVariables;

    public EventData(String id, String action) {
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

    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(Map<String, String> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
