package de.craftmania.dockerized_craft.container_management.docker.bootstrapper;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.EventType;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.core.command.EventsResultCallback;
import de.craftmania.dockerized_craft.container_management.docker.event.EventResultCallback;
import de.craftmania.dockerized_craft.container_management.docker.event.ObservableInterface;
import de.craftmania.dockerized_craft.container_management.docker.event.ObserverInterface;

import java.util.List;
import java.util.logging.Logger;

public class DockerEventBootstrapper implements Runnable, ObservableInterface {
    private DockerClient dockerClient;

    private List<ObserverInterface> observers;

    private String network;

    private Logger logger;


    DockerEventBootstrapper(DockerClient dockerClient, Logger logger, String network) {
        this.dockerClient = dockerClient;
        this.network = network;
        this.logger = logger;
    }

    @Override
    public void run() {
        this.logger.info("[Container Inspector] Running initial inspection.");

        EventsResultCallback callback = new EventResultCallback(
                this.logger,
                this.dockerClient,
                this.network,
                this.observers
        );

        List<Container> containers = this.dockerClient.listContainersCmd().exec();

        // Trigger fake Event to use same Result Callback
        for (Container container: containers) {
            Event event = new Event("start", container.getId(), container.getImage(), System.currentTimeMillis())
                .withAction("bootstrap")
                .withType(EventType.forValue("container"));

            callback.onNext(event);
        }
    }

    @Override
    public void setObservers(List<ObserverInterface> observers) {
        this.observers = observers;
    }
}
