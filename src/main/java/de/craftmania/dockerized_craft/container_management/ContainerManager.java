package de.craftmania.dockerized_craft.container_management;

import com.github.dockerjava.api.DockerClient;
import de.craftmania.dockerized_craft.container_management.connection.BalancedReconnectHandler;
import de.craftmania.dockerized_craft.container_management.connection.session.RedisSessionStorage;
import de.craftmania.dockerized_craft.container_management.connection.session.SessionStorage;
import de.craftmania.dockerized_craft.container_management.notifier.ServerListNotifier;
import de.craftmania.dockerized_craft.container_management.server.DockerEventObserverFactory;
import de.craftmania.dockerized_craft.container_management.connection.Balancer;
import de.craftmania.dockerized_craft.container_management.docker.DockerClientFactory;
import de.craftmania.dockerized_craft.container_management.docker.DockerUtils;
import de.craftmania.dockerized_craft.container_management.docker.event.ObserverInterface;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.*;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class ContainerManager extends Plugin {
    private Configuration configuration;

    @Override
    public void onEnable() {
        try {
            this.loadConfiguration();
        } catch (IOException e) {
            getLogger().warning("Not able to write Configuration File.");
            getLogger().warning("Stopped Plugin enabling (Plugin will not work!)");
            e.printStackTrace();
            return;
        }

        if (this.configuration.getBoolean("connection_balancer.enabled")) {
            this.getLogger().info("[Connection Balancer] Enabled!");
            this.bootstrapConnectionBalancer();
        }

        if (this.configuration.getBoolean("docker.enabled")) {
            this.getLogger().info("[Container Inspector] Enabled!");
            this.bootstrapDockerEvents(this.getDockerEventObservers());
        }

        if (this.configuration.getBoolean("plugin_notifier.enabled")) {
            this.bootstrapPluginNotifier();
        }

    }

    private void bootstrapDockerEvents(List<ObserverInterface> observers) {

        DockerClient dockerClient = DockerClientFactory.getByConfiguration(configuration);
        DockerUtils dockerUtils = new DockerUtils(dockerClient);

        getProxy().getScheduler().runAsync(this,dockerUtils.getRunnableEventBootstrapperWithObservers(
                observers,
                this.configuration.getString("docker.event_listener.network"),
                getLogger()
        ));

        getProxy().getScheduler().runAsync(this, dockerUtils.getRunnableEventSubscriberWithObservers(
                observers,
                this.configuration.getString("docker.event_listener.network"),
                getLogger()
        ));

    }

    private void bootstrapConnectionBalancer() {
        Balancer balancer = new Balancer(
                this.configuration.getSection("connection_balancer.groups"),
                this.configuration.getString("connection_balancer.environment_variables.group_key"),
                this.configuration.getString("connection_balancer.environment_variables.forced_host_key"),
                this.configuration.getSection("connection_balancer.forced_hosts"),
                this.configuration.getString("connection_balancer.default_group"),
                getLogger()
        );

        SessionStorage sessionStorage = new RedisSessionStorage(
                this.configuration.getString("connection_balancer.player_session_store.redis.host"),
                this.configuration.getInt("connection_balancer.player_session_store.redis.port"),
                this.configuration.getBoolean("connection_balancer.player_session_store.redis.ssl")
        );
        ReconnectHandler reconnectHandler = new BalancedReconnectHandler(balancer ,getProxy(), sessionStorage);
        getProxy().setReconnectHandler(reconnectHandler);
        getProxy().getPluginManager().registerListener(this, balancer);
    }


    private void loadConfiguration() throws IOException {

        if (!getDataFolder().exists()) {
            if (!getDataFolder().mkdir()) {
                throw new IOException("Not able to generate Plugin Data Folder");
            }
        }

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.configuration = ConfigurationProvider.getProvider(
                    YamlConfiguration.class).load(new File(getDataFolder(), "config.yml")
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unused")
    public Configuration getConfiguration() {
        return configuration;
    }

    private List<ObserverInterface> getDockerEventObservers() {

        List<ObserverInterface> observers = new ArrayList<>(1);

        observers.add(DockerEventObserverFactory.getByConfiguration(
                this.configuration,
                getProxy(),
                getLogger()
        ));

        return observers;
    }

    private void bootstrapPluginNotifier() {
        ServerListNotifier notifier = new ServerListNotifier(
                this.configuration.getSection("plugin_notifier.meta_data_mapper"),
                getLogger()
        );

        getProxy().getPluginManager().registerListener(this, notifier);
        getProxy().getScheduler().schedule(this, () -> {
            notifier.sendUpdate();
        }, 0, 10, TimeUnit.SECONDS);
    }

}
