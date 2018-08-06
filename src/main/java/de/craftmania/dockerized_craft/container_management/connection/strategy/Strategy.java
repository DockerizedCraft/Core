package de.craftmania.dockerized_craft.container_management.connection.strategy;

import net.md_5.bungee.api.config.ServerInfo;

import java.util.Map;

public interface Strategy {
    public ServerInfo getServer(Map<String, ServerInfo> server);
}
