package de.craftmania.dockerized_craft.container_management.connection.strategy;

import net.md_5.bungee.api.config.ServerInfo;

import java.util.Map;

public class BalanceStrategy implements Strategy {
    @Override
    public ServerInfo getServer(Map<String, ServerInfo> server) {
        ServerInfo selectedServer = null;

        for (String key: server.keySet()) {
            if (selectedServer == null) {
                selectedServer = server.get(key);
            } else if (selectedServer.getPlayers().size() > server.get(key).getPlayers().size()) {
                selectedServer = server.get(key);
            }
        }

        return selectedServer;
    }
}
