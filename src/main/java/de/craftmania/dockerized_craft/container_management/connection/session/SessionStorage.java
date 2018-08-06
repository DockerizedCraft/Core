package de.craftmania.dockerized_craft.container_management.connection.session;

import net.md_5.bungee.api.config.ServerInfo;

import java.util.UUID;

public interface SessionStorage {
    public void setReconnectServer(UUID uuid, String serverName);

    public String getReconnectServer(UUID uuid);
}
