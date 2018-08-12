package de.craftmania.dockerizedcraft.connection.balancer.session;

import java.util.UUID;

public interface SessionStorage {
    void setReconnectServer(UUID uuid, String serverName);

    String getReconnectServer(UUID uuid);
}
