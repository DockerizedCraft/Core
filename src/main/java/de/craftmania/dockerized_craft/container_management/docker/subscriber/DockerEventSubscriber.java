package de.craftmania.dockerized_craft.container_management.docker.subscriber;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.command.EventsResultCallback;
import de.craftmania.dockerized_craft.container_management.docker.event.EventResultCallback;
import de.craftmania.dockerized_craft.container_management.docker.event.ObservableInterface;
import de.craftmania.dockerized_craft.container_management.docker.event.ObserverInterface;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class DockerEventSubscriber implements Runnable, ObservableInterface {

    private DockerClient client;

    private Logger logger;

    private String network;

    private List<ObserverInterface> observers;

    DockerEventSubscriber(DockerClient client, Logger logger, String network) {
        this.client = client;
        this.logger = logger;
        this.network = network;
    }

    @Override
    public void run() {
        EventsResultCallback callback = new EventResultCallback(
                this.logger,
                this.client,
                this.network,
                this.observers
        );

        try {
            this.client.eventsCmd().exec(callback).awaitCompletion().close();
        } catch (IOException |InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setObservers(List<ObserverInterface> observers) {
        this.observers = observers;
    }
}
