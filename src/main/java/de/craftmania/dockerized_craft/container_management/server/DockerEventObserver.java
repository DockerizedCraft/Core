package de.craftmania.dockerized_craft.container_management.server;


import de.craftmania.dockerized_craft.container_management.server.events.PostAddServerEvent;
import de.craftmania.dockerized_craft.container_management.server.events.PostRemoveServerEvent;
import de.craftmania.dockerized_craft.container_management.server.events.PreAddServerEvent;
import de.craftmania.dockerized_craft.container_management.server.events.PreRemoveServerEvent;
import de.craftmania.dockerized_craft.container_management.docker.event.ObserverInterface;
import de.craftmania.dockerized_craft.container_management.docker.event.model.EventData;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.logging.Logger;

public class DockerEventObserver implements ObserverInterface {
    private ProxyServer proxyServer;

    private String identifier;

    private String nameKey;

    private String portKey;

    private String motdKey;

    private String restrictedKey;

    private List<String> addActionList;

    private List<String> removeActionList;

    private Logger logger;

    private Boolean debug;

    DockerEventObserver(
            ProxyServer proxyServer,
            String identifier,
            String nameKey,
            String portKey,
            String motdKey,
            String restrictedKey,
            Boolean debug,
            Logger logger
    ) {
        this.proxyServer = proxyServer;
        this.identifier = identifier;
        this.nameKey = nameKey;
        this.portKey = portKey;
        this.motdKey = motdKey;
        this.restrictedKey = restrictedKey;
        this.logger = logger;
        this.debug = debug;
    }

    void setAddActionList(List<String> addActionList) {
        this.addActionList = addActionList;
    }

    void setRemoveActionList(List<String> removeActionList) {
        this.removeActionList = removeActionList;
    }

    public void onEvent(EventData eventData) throws Exception {

        if (!eventData.getEnvironmentVariables().containsKey(this.identifier)) {
            return;
        }

        if (this.addActionList.contains(eventData.getAction())) {
            this.addServer(eventData);
        }

        else if(this.removeActionList.contains(eventData.getAction())) {
            this.removeServer(eventData);
        }

    }

    private void addServer(EventData eventData) {

        ServerInfo serverInfo = this.getServerInfoForEvent(eventData);

        if (serverInfo.getAddress().getHostName() == null) {
            this.logger.warning("[Container Inspector] Could not add server:" + serverInfo.getName());
            this.logger.warning("[Container Inspector]  > Reason: No IP, is you network fine?");
            this.logger.warning("[Container Inspector]  > Trigger-Event-Action: " + eventData.getAction());

            return;
        }

        if (this.proxyServer.getServers().containsKey(serverInfo.getName())) {
            if (this.debug) {
                this.logger.warning("[Container Inspector] Server with id " + serverInfo.getName() + " already exists in Bungeecord Proxy.");
            }

            InetSocketAddress currentAddress = this.proxyServer.getServers().get(serverInfo.getName()).getAddress();

            if (!currentAddress.equals(serverInfo.getAddress())) {
                if (this.debug) {
                    this.logger.warning("[Container Inspector]  > Server address of " + serverInfo.getName() + "changed!");
                    this.logger.warning("[Container Inspector]  >> Current: " + currentAddress.toString());
                    this.logger.warning("[Container Inspector]  >> New: " + serverInfo.getAddress().toString());
                    this.logger.warning("[Container Inspector]  >> Server removed from proxy to re-add it");
                }
                this.proxyServer.getServers().remove(serverInfo.getName());
            } else {
                if (this.debug) {
                    this.logger.warning("[Container Inspector]  > Skipped!");
                    this.logger.warning("[Container Inspector]  > Trigger-Event-Action: " + eventData.getAction());
                }
                return;
            }
        }


        this.proxyServer.getPluginManager().callEvent(new PreAddServerEvent(
                serverInfo,
                eventData.getEnvironmentVariables()
        ));

        this.proxyServer.getServers().put(serverInfo.getName(), serverInfo);
        this.logger.info("[Container Inspector] Added server: " + serverInfo.getName());
        this.logger.info("[Container Inspector]  > Address: " + serverInfo.getAddress().toString());
        this.logger.info("[Container Inspector]  > MOTD: " + serverInfo.getMotd());
        this.logger.info("[Container Inspector]  > Trigger-Event-Action: " + eventData.getAction());

        this.proxyServer.getPluginManager().callEvent(new PostAddServerEvent(
                serverInfo,
                eventData.getEnvironmentVariables()
        ));
    }

    private void removeServer (EventData eventData) {
        // server id
        String id = this.getServerId(eventData);

        if (!this.proxyServer.getServers().containsKey(id)) {
            if(this.debug) {
                this.logger.warning("[Container Inspector] Could not remove server: " + id);
                this.logger.warning("[Container Inspector]  > Reason: Not exists");
                this.logger.warning("[Container Inspector]  > Trigger-Event-Action: " + eventData.getAction());
            }

            return;
        }

        this.proxyServer.getPluginManager().callEvent(new PreRemoveServerEvent(
                this.getServerInfoForEvent(eventData),
                eventData.getEnvironmentVariables()
        ));

        this.proxyServer.getServers().remove(id);
        this.logger.info("[Container Inspector] Removing Server: " + id);
        this.logger.info("[Container Inspector]  > Trigger-Event-Action: " + eventData.getAction());

        this.proxyServer.getPluginManager().callEvent(new PostRemoveServerEvent(id));
    }

    private ServerInfo getServerInfoForEvent(EventData eventData) {
        // server id
        String id = this.getServerId(eventData);

        // Getting the address to create
        int port = eventData.getEnvironmentVariables().get(this.portKey) != null
            ? Integer.parseInt(eventData.getEnvironmentVariables().get(this.portKey))
            : (eventData.getPort() != null ? eventData.getPort() : 25565);

        InetSocketAddress inetSocketAddress = new InetSocketAddress(eventData.getIp(), port);


        // Getting the motd
        String motd = eventData.getEnvironmentVariables().get(this.motdKey) != null
            ? eventData.getEnvironmentVariables().get(this.motdKey)
            : "A Minecraft Server Instance";

        // Getting restricted bool
        boolean restricted =
                eventData.getEnvironmentVariables().get(this.restrictedKey) != null &&
                        eventData.getEnvironmentVariables().get(this.restrictedKey).equals("restricted");

        return ProxyServer.getInstance().constructServerInfo(
                id,
                inetSocketAddress,
                motd,
                restricted
        );
    }

    private String getServerId(EventData eventData) {
        if (eventData.getEnvironmentVariables().get(this.nameKey) != null) {
            return eventData.getEnvironmentVariables().get(this.nameKey);
        }

        if (eventData.getName() != null) {
            return eventData.getName().replace("/", "");
        }

        return eventData.getId();
    }

}
