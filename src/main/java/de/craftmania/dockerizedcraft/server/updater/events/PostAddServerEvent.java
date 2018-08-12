package de.craftmania.dockerizedcraft.server.updater.events;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Event;

import java.util.Map;

public class PostAddServerEvent extends Event {
    private ServerInfo serverInfo;
    private Map<String, String> environmentVariables;

    public PostAddServerEvent(ServerInfo serverInfo, Map<String, String> environmentVariables) {
        this.serverInfo = serverInfo;
        this.environmentVariables = environmentVariables;
    }

    @SuppressWarnings("unused")
    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    @SuppressWarnings("unused")
    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }
}
