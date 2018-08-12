package de.craftmania.dockerizedcraft.server.updater.events;

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
