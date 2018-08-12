package de.craftmania.dockerizedcraft;

import de.craftmania.dockerizedcraft.connection.balancer.BalancedReconnectHandler;
import de.craftmania.dockerizedcraft.connection.balancer.ConnectionBalancer;
import de.craftmania.dockerizedcraft.connection.balancer.session.RedisSessionStorage;
import de.craftmania.dockerizedcraft.connection.balancer.session.SessionStorage;
import de.craftmania.dockerizedcraft.container.inspector.ContainerInspector;
import de.craftmania.dockerizedcraft.plugin.notifier.serverlist.ServerListPluginNotifier;
import de.craftmania.dockerizedcraft.server.updater.ServerUpdater;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.*;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;


@SuppressWarnings("unused")
public class ContainerManager extends Plugin {
    private Map<String, Configuration> configuration;

    /**
     * Enabled sub-packages depending on the given configuration
     */
    @Override
    public void onEnable() {
        try {
            this.loadConfiguration();
        } catch (IOException e) {
            getLogger().warning("Not able to write Configuration File.");
            getLogger().warning("Check write Permissions to the plugin directory.");
            getLogger().warning("Stopped Plugin enabling (Plugin will not work!)");
            e.printStackTrace();
            return;
        }

        if (getConfiguration().get("connection-balancer").getBoolean("enabled")) {
            getLogger().info("[Connection ConnectionBalancer] Enabled!");
            bootstrapConnectionBalancer(getConfiguration().get("connection-balancer"));
        }


        if (getConfiguration().get("server-updater").getBoolean("enabled")) {
            getLogger().info("[Server Updater] Enabled!");
            bootstrapServerUpdater(getConfiguration().get("server-updater"));
        }

        if (getConfiguration().get("plugin-notifier").getBoolean("enabled")) {
            getLogger().info("[Plugin Notification] Enabled!");
            bootstrapPluginNotifier(getConfiguration().get("plugin-notifier"));
        }

        if (getConfiguration().get("container-inspector").getBoolean("enabled")) {
            getLogger().info("[Container Inspector] Enabled!");
            bootstrapContainerInspector(
                    getConfiguration().get("container-inspector")
            );
        }
    }

    /**
     * Bootstraps the Connection Balancer, sets the reconnect handler and adds the registered listener
     * @param configuration The connection balancer configuration
     */
    private void bootstrapConnectionBalancer(Configuration configuration) {
        ConnectionBalancer connectionBalancer = new ConnectionBalancer(
                configuration,
                getLogger()
        );

        SessionStorage sessionStorage = new RedisSessionStorage(
                configuration.getString("player_session_store.redis.host"),
                configuration.getInt("player_session_store.redis.port"),
                configuration.getBoolean("player_session_store.redis.ssl")
        );

        ReconnectHandler reconnectHandler = new BalancedReconnectHandler(connectionBalancer, sessionStorage);
        getProxy().setReconnectHandler(reconnectHandler);
        getProxy().getPluginManager().registerListener(this, connectionBalancer);
    }

    /**
     * Bootstraps the server update and adds it the the registered listeners
     * @param configuration The server updater configuration
     */
    private void bootstrapServerUpdater(Configuration configuration) {
        ServerUpdater serverUpdater = new ServerUpdater(configuration, getProxy(), getLogger());
        getProxy().getPluginManager().registerListener(this, serverUpdater);
    }

    /**
     * Bootstraps the plugin notifier and adds scheduled interval tasks
     * @param configuration The plugin notifier configuration
     */
    private void bootstrapPluginNotifier(Configuration configuration) {
        ServerListPluginNotifier notifier = new ServerListPluginNotifier(
                configuration.getSection("meta-data-mapper"),
                getLogger()
        );

        getProxy().getPluginManager().registerListener(this, notifier);
        getProxy().getScheduler().schedule(
                this,
                notifier::sendUpdate,
                0,
                configuration.getInt("refresh-interval"),
                TimeUnit.SECONDS
        );
    }

    /**
     * Bootstraps the container inspector and runs the inspector and listener as async task through the scheduler
     * @param configuration The container inspector configuration
     */
    private void bootstrapContainerInspector(Configuration configuration) {

        ContainerInspector containerInspector = new ContainerInspector(configuration, getProxy(), getLogger());

        getProxy().getScheduler().runAsync(this, containerInspector::runContainerInspection);
        getProxy().getScheduler().runAsync(this, containerInspector::runContainerListener);
    }

    /**
     * Loads the configurations
     * @throws IOException On missing write access
     */
    private void loadConfiguration() throws IOException {

        List<String> configNames = Arrays.asList(
                "connection-balancer",
                "plugin-notifier",
                "container-inspector",
                "server-updater"
        );
        Map<String, Configuration> configuration = new HashMap<>(configNames.size());


        if (!getDataFolder().exists()) {
            if (!getDataFolder().mkdir()) {
                throw new IOException("Not able to generate Plugin Data Folder");
            }
        }


        for (String configName : configNames) {


            File file = new File(getDataFolder(), configName + ".yml");

            if (!file.exists()) {
                try (InputStream in = getResourceAsStream(configName + ".yml")) {
                    Files.copy(in, file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            configuration.put(configName, ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(getDataFolder(), configName + ".yml")
                    ));
        }

        this.configuration = configuration;
    }

    /**
     * @return Map containing the sub-package configurations
     */
    private Map<String, Configuration> getConfiguration() {
        return configuration;
    }

}
