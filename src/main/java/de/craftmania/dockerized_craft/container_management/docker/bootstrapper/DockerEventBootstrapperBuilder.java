package de.craftmania.dockerized_craft.container_management.docker.bootstrapper;

import com.github.dockerjava.api.DockerClient;
import de.craftmania.dockerized_craft.container_management.docker.event.ObserverInterface;

import java.util.List;
import java.util.logging.Logger;

public class DockerEventBootstrapperBuilder {
    private List<ObserverInterface> observers;


    public DockerEventBootstrapperBuilder withObservers(List<ObserverInterface> observers) {
        this.observers = observers;

        return this;
    }
    public DockerEventBootstrapper create(DockerClient client, Logger logger, String network) {
        DockerEventBootstrapper dockerEventSubscriber = new DockerEventBootstrapper(
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
