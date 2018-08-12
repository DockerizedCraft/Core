package de.craftmania.dockerizedcraft.connection.balancer;

import de.craftmania.dockerizedcraft.connection.balancer.strategy.BalanceStrategy;
import de.craftmania.dockerizedcraft.connection.balancer.strategy.Strategy;
import de.craftmania.dockerizedcraft.server.updater.events.PostAddServerEvent;
import de.craftmania.dockerizedcraft.server.updater.events.PreRemoveServerEvent;
import de.craftmania.dockerizedcraft.connection.balancer.model.Group;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ConnectionBalancer implements Listener {

    private static String defaultGroupName;
    static {
        defaultGroupName = "default";
    }

    private String groupEnvironmentVariable;
    private Group defaultGroup;
    private Map<String, Group> groups;

    private Map<String, ServerInfo> servers;
    private Map<String, String> serverGroups;

    private String forcedHostEnvironmentVariable;
    private Map<String, ServerInfo> forcedHostServers;
    private Map<String, Group> forcedHostGroups;

    private Logger logger;

    public static final Map<String, Strategy> balanceStrategies;
    static {
        Map<String, Strategy> strategies = new HashMap<>(1);
        strategies.put("balance", new BalanceStrategy());
        balanceStrategies = Collections.unmodifiableMap(strategies);
    }

    public ConnectionBalancer(
            Configuration configuration,
            Logger logger
    ) {
        this.servers = new HashMap<>();
        this.serverGroups = new HashMap<>();
        this.forcedHostServers = new HashMap<>();

        this.groupEnvironmentVariable =  configuration.getString("environment-variables.group");
        this.forcedHostEnvironmentVariable = configuration.getString("environment-variables.forced_host");

        this.groups = this.getGroupsByConfiguration(configuration.getSection("groups"));
        this.defaultGroup =
            this.getGroup(configuration.getString("default-group")) != null
            ? this.getGroup(configuration.getString("default-group"))
            : this.getGroup(ConnectionBalancer.defaultGroupName);

        this.forcedHostGroups = this.getGroupsForcedHosts(this.groups, configuration.getSection("forced-hosts"));

        this.logger = logger;

        assert this.defaultGroup != null;
        this.logger.info("[Connection Balancer] Added " + this.groups.size() + " server groups.");
        this.logger.info("[Connection Balancer] Setting default group: " + this.defaultGroup.getName());
    }

    @SuppressWarnings("WeakerAccess")
    public ServerInfo getReconnectServer(String name) {
        if (this.servers.containsKey(name)) {
            if (this.groups.get(this.serverGroups.get(name)).getCanReconnect()) {
                return this.servers.get(name);
            }

            return null;
        }

        return null;
    }

    @SuppressWarnings("WeakerAccess")
    public ServerInfo getForcedServer(String hostname) {
        if (this.forcedHostGroups.containsKey(hostname)) {
            return this.forcedHostGroups.get(hostname).getStrategy().getServer(this.forcedHostGroups.get(hostname).getServers());
        }

        if(this.forcedHostServers.containsKey(hostname)) {
            return this.forcedHostServers.get(hostname);
        }

        return null;
    }

    @SuppressWarnings("WeakerAccess")
    public ServerInfo getFallbackServer() {
        return this.defaultGroup.getStrategy().getServer(this.defaultGroup.getServers());
    }

    private Group getGroup(String name) {
        if (this.groups.containsKey(name)) {
            return this.groups.get(name);
        }

        return null;
    }

    private Map<String, Group> getGroupsForcedHosts(Map<String, Group> groups, Configuration forcedHost) {
        Map<String, Group> forcedHosts = new HashMap<>(forcedHost.getKeys().size());
        for (String key : forcedHost.getKeys()) {
            if (groups.containsKey(forcedHost.getString(key))) {
                forcedHost.set(key, groups.get(forcedHost.getString(key)));
            }
        }

        return forcedHosts;
    }

    private  Map<String, Group> getGroupsByConfiguration(Configuration groupConfig) {
        // Add default group if not configured
        if (!groupConfig.contains(ConnectionBalancer.defaultGroupName)) {

            Configuration defaultGroupConfig = new Configuration();
            defaultGroupConfig.set("strategy", "balance");
            defaultGroupConfig.set("can-reconnect", true);
            defaultGroupConfig.set("restricted", false);
            groupConfig.set(ConnectionBalancer.defaultGroupName, defaultGroupConfig);
        }

        // Reset groups maps
        Map<String, Group> groups = new HashMap<>(groupConfig.getKeys().size());

        // Now lets add configured groups
        for (String key : groupConfig.getKeys()) {
            groups.put(key, new Group(
                    key,
                    groupConfig.getString(key + ".strategy"),
                    groupConfig.getBoolean(key + ".restricted"),
                    groupConfig.getBoolean(key + ".can_reconnect")
            ));
        }

        return groups;
    }

    @SuppressWarnings("WeakerAccess")
    public void addServer(ServerInfo server) {
        this.servers.put(server.getName(), server);
    }

    @SuppressWarnings("WeakerAccess")
    public void removeServer(ServerInfo server) {
        this.servers.remove(server.getName());
    }

    @SuppressWarnings("unused")
    public void removeServer(String serverName) {
        this.servers.remove(serverName);
    }

    @SuppressWarnings("unused")
    public ServerInfo getServer(ServerInfo server) {
        return this.servers.get(server.getName());
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public ServerInfo getServer(String name) {
        return this.servers.get(name);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPostAddServer(PostAddServerEvent event) {
        String groupName = ConnectionBalancer.defaultGroupName;
        if (event.getEnvironmentVariables().containsKey(this.groupEnvironmentVariable)) {
            groupName = event.getEnvironmentVariables().get(this.groupEnvironmentVariable);
        }

        if (!this.groups.containsKey(groupName)) {
            groupName = ConnectionBalancer.defaultGroupName;
        }

        this.addServer(event.getServerInfo());
        this.logger.info("[Connection Balancer] Added Server: " + event.getServerInfo().getName());
        this.groups.get(groupName).addServer(event.getServerInfo());
        this.serverGroups.put(event.getServerInfo().getName(), groupName);
        this.logger.info("[Connection Balancer] Added Server to group: " + event.getServerInfo().getName() + " > " + groupName);
        if  (event.getEnvironmentVariables().containsKey(this.forcedHostEnvironmentVariable)) {
            String forcedHost = event.getEnvironmentVariables().get(this.forcedHostEnvironmentVariable);

            if (this.forcedHostServers.containsKey(forcedHost)) {
                this.logger.warning(
                "[Connection Balancer] Overwriting existing Forced Host: "
                        + forcedHost
                        + " > "
                        + this.forcedHostServers.get(forcedHost).getName()
                );
            }

            this.logger.info("[Connection Balancer] Adding Server Forced Host: "
                    + forcedHost
                    + " > "
                    + event.getServerInfo().getName());
            this.forcedHostServers.put(
                    forcedHost,
                    event.getServerInfo()
            );
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPreRemoveServer(PreRemoveServerEvent event) {
        if (!this.servers.containsKey(event.getServerInfo().getName())) {
            return;
        }
        ServerInfo server = this.servers.get(event.getServerInfo().getName());

        this.logger.info("[Connection Balancer] Removing Server from group: " + event.getServerInfo().getName() + " < " + this.serverGroups.get(server.getName()));
        this.groups.get(this.serverGroups.get(server.getName())).removeServer(server);
        this.serverGroups.remove(server.getName());
        this.logger.info("[Connection Balancer] Removing Server: " + event.getServerInfo().getName());
        this.removeServer(server);

        for (String entry: this.forcedHostServers.keySet()) {
            if (this.forcedHostServers.get(entry).getName().equals(server.getName())) {
                this.logger.info("[Connection Balancer] Removing Server Forced Host: "
                        + entry
                        + " > "
                        + server.getName());

                this.forcedHostServers.remove(entry);
                break;
            }
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPluginMessage(PluginMessageEvent event) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        try {
            String subchanncel = in.readUTF();
            this.logger.info(subchanncel);
        } catch (Exception ignored) {
        }
    }
}
