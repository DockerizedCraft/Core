package de.craftmania.dockerizedcraft.plugin.notifier.serverlist;


import com.google.gson.JsonObject;
import de.craftmania.dockerizedcraft.plugin.notifier.AbstractNotifier;
import de.craftmania.dockerizedcraft.server.updater.events.PostAddServerEvent;
import de.craftmania.dockerizedcraft.server.updater.events.PreRemoveServerEvent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ServerListPluginNotifier extends AbstractNotifier implements Listener {
    private static String channel;
    static {
        channel = "DockerizedCraft";
    }

    private Configuration mappingConfig;
    private Logger logger;

    private JsonObject serverInfos;
    private Map<String, ServerInfo> servers;

    public ServerListPluginNotifier(Configuration mappingConfig, Logger logger) {
        this.logger = logger;
        this.mappingConfig = mappingConfig;
        this.serverInfos = new JsonObject();
        this.servers = new HashMap<>();
    }

    public void sendUpdate() {
        this.sendServerList(this.servers);
    }

    private void sendServerList(Map<String, ServerInfo> servers) {
        this.logger.info("[Plugin Notification] Sending update to servers");
        for (String serverName: servers.keySet()) {
            this.sendMessage(
                    this.servers.get(serverName),
                    ServerListPluginNotifier.channel,
                    "ServerData",
                    this.serverInfos.toString()
            );
        }

    }

    private JsonObject getMetaData(ServerInfo server, Map<String, String> environmentVariables) {
        JsonObject serverMetaData = new JsonObject();
        serverMetaData.addProperty("address", server.getAddress().getAddress().getHostAddress() + ':' + server.getAddress().getPort());
        serverMetaData.addProperty("host", server.getAddress().getAddress().getHostAddress());
        serverMetaData.addProperty("port", server.getAddress().getPort());
        serverMetaData.addProperty("motd", server.getMotd());
        serverMetaData.addProperty("name", server.getName());
        serverMetaData.addProperty("proxied_players", server.getPlayers().size());

        for (String configKey: this.mappingConfig.getKeys()) {
            Configuration metaConfig = this.mappingConfig.getSection(configKey);
            String value = null;

            if (environmentVariables.containsKey(metaConfig.getString("environment-variable"))) {
                value = environmentVariables.get(metaConfig.getString("environment-variable"));
            } else if (metaConfig.contains("default")) {
                value = metaConfig.getString("default");
            }

            if (value == null) {
                if (metaConfig.getBoolean("required")) {
                    serverMetaData.addProperty(configKey, (String) null);
                }
            } else {
                serverMetaData.addProperty(configKey, value);
            }
        }

        return serverMetaData;
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPostAddServer(PostAddServerEvent event) {
        this.serverInfos.add(event.getServerInfo().getName(), this.getMetaData(event.getServerInfo(), event.getEnvironmentVariables()));
        this.servers.put(event.getServerInfo().getName(), event.getServerInfo());

        this.logger.info("[Plugin Notification] Added Server meta data: " + event.getServerInfo().getName());
        this.sendServerList(this.servers);

    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPreRemoveServer(PreRemoveServerEvent event) {
        this.serverInfos.remove(event.getServerInfo().getName());
        this.servers.remove(event.getServerInfo().getName());

        this.logger.info("[Plugin Notification] Removed Server meta data: " + event.getServerInfo().getName());
        this.sendServerList(this.servers);
    }
}
