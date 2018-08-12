package de.craftmania.dockerizedcraft.connection.balancer;

import com.google.common.base.Preconditions;
import de.craftmania.dockerizedcraft.connection.balancer.session.SessionStorage;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BalancedReconnectHandler implements ReconnectHandler {
    private ConnectionBalancer connectionBalancer;
    private SessionStorage sessionStorage;

    public BalancedReconnectHandler(ConnectionBalancer connectionBalancer, SessionStorage sessionStorage) {
        this.connectionBalancer = connectionBalancer;
        this.sessionStorage = sessionStorage;
    }

    @Override
    public ServerInfo getServer(ProxiedPlayer proxiedPlayer) {
        ServerInfo serverInfo = null;

        if (proxiedPlayer.getPendingConnection().getVirtualHost().getHostName() != null) {
            serverInfo = this.connectionBalancer.getForcedServer(proxiedPlayer.getPendingConnection().getVirtualHost().getHostName());
        }

        if (serverInfo == null && this.sessionStorage.getReconnectServer(proxiedPlayer.getUniqueId()) != null) {
            serverInfo = this.connectionBalancer.getReconnectServer(this.sessionStorage.getReconnectServer(proxiedPlayer.getUniqueId()));
        }

        if (serverInfo == null) {
            serverInfo = this.connectionBalancer.getFallbackServer();
        }

        Preconditions.checkState( serverInfo != null, "Default server or group not defined" );

        return serverInfo;
    }

    @Override
    public void setServer(ProxiedPlayer proxiedPlayer) {
        this.sessionStorage.setReconnectServer(
            proxiedPlayer.getUniqueId(),
            ( proxiedPlayer.getReconnectServer() != null )
                ? proxiedPlayer.getReconnectServer().getName()
                : proxiedPlayer.getServer().getInfo().getName()
        );
    }

    @Override
    public void save() {

    }

    @Override
    public void close() {
    }

}

