package de.craftmania.dockerized_craft.container_management.docker.subscriber;

import com.github.dockerjava.api.DockerClient;
import de.craftmania.dockerized_craft.container_management.docker.event.ObserverInterface;

import java.util.List;
import java.util.logging.Logger;

public class DockerEventSubscriberBuilder {
    private List<ObserverInterface> observers;


    public DockerEventSubscriberBuilder withObservers(List<ObserverInterface> observers) {
        this.observers = observers;

        return this;
    }

    public DockerEventSubscriber create(DockerClient client, Logger logger, String network) {
        DockerEventSubscriber dockerEventSubscriber = new DockerEventSubscriber(
                client,
                logger,
                network
        );

        if (this.observers != null) {
            dockerEventSubscriber.setObservers(this.observers);
        }

        return dockerEventSubscriber;
    }
}
