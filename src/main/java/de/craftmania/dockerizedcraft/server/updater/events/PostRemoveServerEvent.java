package de.craftmania.dockerizedcraft.server.updater.events;

import net.md_5.bungee.api.plugin.Event;

public class PostRemoveServerEvent extends Event {

    private String name;

    @SuppressWarnings("unused")
    public PostRemoveServerEvent(String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }
}
