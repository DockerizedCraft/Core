package de.craftmania.dockerized_craft.container_management.docker.event;

import de.craftmania.dockerized_craft.container_management.docker.event.model.EventData;

public interface ObserverInterface {
    void onEvent(EventData eventData) throws Exception;
}
