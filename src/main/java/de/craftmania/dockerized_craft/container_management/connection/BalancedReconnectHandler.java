package de.craftmania.dockerized_craft.container_management.connection;

import com.google.common.base.Preconditions;
import de.craftmania.dockerized_craft.container_management.connection.session.SessionStorage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BalancedReconnectHandler implements ReconnectHandler {
    private Balancer balancer;
    private ProxyServer proxyServer;
    private SessionStorage sessionStorage;

    public BalancedReconnectHandler(Balancer balancer, ProxyServer proxyServer, SessionStorage sessionStorage) {
        this.balancer = balancer;
        this.proxyServer = proxyServer;
        this.sessionStorage = sessionStorage;
    }

    @Override
    public ServerInfo getServer(ProxiedPlayer proxiedPlayer) {
        ServerInfo serverInfo = null;

        if (proxiedPlayer.getPendingConnection().getVirtualHost().getHostName() != null) {
            serverInfo = this.balancer.getForcedServer(proxiedPlayer.getPendingConnection().getVirtualHost().getHostName());
        }

        if (serverInfo == null && this.sessionStorage.getReconnectServer(proxiedPlayer.getUniqueId()) != null) {
            serverInfo = this.balancer.getReconnectServer(this.sessionStorage.getReconnectServer(proxiedPlayer.getUniqueId()));
        }

        if (serverInfo == null) {
            serverInfo = this.balancer.getFallbackServer();
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

