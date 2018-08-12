package de.craftmania.dockerizedcraft.connection.balancer.strategy;

import net.md_5.bungee.api.config.ServerInfo;

import java.util.Map;

public interface Strategy {
    ServerInfo getServer(Map<String, ServerInfo> server);
}
