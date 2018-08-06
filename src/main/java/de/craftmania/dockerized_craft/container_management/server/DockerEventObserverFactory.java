package de.craftmania.dockerized_craft.container_management.server;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;

import java.util.logging.Logger;

public class DockerEventObserverFactory {
    public static DockerEventObserver getByConfiguration(Configuration configuration, ProxyServer proxy, Logger logger) {

        DockerEventObserver bungeeCordObserver = new DockerEventObserver(
                proxy,
                configuration.getString("docker.event_listener.environment_variables.identifier"),
                configuration.getString("docker.event_listener.environment_variables.name_key"),
                configuration.getString("docker.event_listener.environment_variables.port_key"),
                configuration.getString("docker.event_listener.environment_variables.motd_key"),
                configuration.getString("docker.event_listener.environment_variables.restricted_key"),
                configuration.getBoolean("debug.docker_event_observer"),
                logger
        );

        bungeeCordObserver.setAddActionList(
                configuration.getStringList("docker.event_listener.add_actions")
        );

        bungeeCordObserver.setRemoveActionList(
                configuration.getStringList("docker.event_listener.remove_actions")
        );

        return bungeeCordObserver;
    }
}
