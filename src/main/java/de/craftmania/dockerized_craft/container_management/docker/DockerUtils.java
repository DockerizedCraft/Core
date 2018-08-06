package de.craftmania.dockerized_craft.container_management.docker;

import com.github.dockerjava.api.DockerClient;
import de.craftmania.dockerized_craft.container_management.docker.bootstrapper.DockerEventBootstrapperBuilder;
import de.craftmania.dockerized_craft.container_management.docker.subscriber.DockerEventSubscriberBuilder;
import de.craftmania.dockerized_craft.container_management.docker.event.ObserverInterface;

import java.util.List;
import java.util.logging.Logger;

public class DockerUtils {

    private DockerClient dockerClient;

    public DockerUtils(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public Runnable getRunnableEventSubscriberWithObservers(List<ObserverInterface> observers, String network, Logger logger) {
        return new DockerEventSubscriberBuilder()
            .withObservers(observers)
            .create(this.dockerClient, logger, network);
    }

    public Runnable getRunnableEventBootstrapperWithObservers(List<ObserverInterface> observers, String network, Logger logger) {
        return new DockerEventBootstrapperBuilder()
                .withObservers(observers)
                .create(this.dockerClient, logger, network);
    }
}
