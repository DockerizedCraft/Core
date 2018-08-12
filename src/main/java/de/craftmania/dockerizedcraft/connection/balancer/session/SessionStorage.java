package de.craftmania.dockerizedcraft.connection.balancer.session;

import java.util.UUID;

public interface SessionStorage {
    public void setReconnectServer(UUID uuid, String serverName);

    public String getReconnectServer(UUID uuid);
}
