package de.craftmania.dockerized_craft.container_management.server.events;

import net.md_5.bungee.api.plugin.Event;

public class PostRemoveServerEvent extends Event {

    private String name;

    public PostRemoveServerEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
