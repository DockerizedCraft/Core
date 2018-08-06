package de.craftmania.dockerized_craft.container_management.docker.event;

import java.util.List;

public interface ObservableInterface {
    void setObservers(List<ObserverInterface> observers);
}
