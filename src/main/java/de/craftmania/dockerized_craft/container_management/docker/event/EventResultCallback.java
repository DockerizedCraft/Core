package de.craftmania.dockerized_craft.container_management.docker.event;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.command.EventsResultCallback;
import de.craftmania.dockerized_craft.container_management.docker.event.model.EventData;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Logger;

public class EventResultCallback extends EventsResultCallback {
    private Logger logger;
    private DockerClient dockerClient;
    private String network;
    private List<ObserverInterface> observers;

    public EventResultCallback(Logger logger, DockerClient dockerClient, String network, List<ObserverInterface> observers) {
        this.logger = logger;
        this.dockerClient = dockerClient;
        this.network = network;
        this.observers = observers;
    }

    @Override
    public void onNext(Event event) {
        if (this.observers == null) {
            return;
        }

        for (ObserverInterface observer : this.observers) {
            if (event.getType() == null || !event.getType().getValue().equals("container")) {
                return;
            }

            EventData eventData = new EventData(event.getId(), event.getAction());

            try {
                InspectContainerResponse info = this.dockerClient.inspectContainerCmd(event.getId()).exec();

                eventData.setName(info.getName());
                eventData.setEnvironmentVariables(this.getEnvironmentVariables(info));
                eventData.setPort(this.getPort(info));
                eventData.setIp(this.getIp(info, this.network));

            } catch (NotFoundException e) {
                return;
            }

            try {
                observer.onEvent(eventData);
            } catch (Exception e) {
                this.logger.warning("Could not listen on docker event for container: " + eventData.getId() + " Action: " + eventData.getAction());
                this.logger.warning(e.getMessage());
            }
        }

        super.onNext(event);
    }


    private Integer getPort(InspectContainerResponse info) {
        Map<ExposedPort, Ports.Binding[]> portBindings = info.getNetworkSettings().getPorts().getBindings();

        if (portBindings.keySet().size() > 0 && portBindings.keySet().iterator().next().getPort() != 0) {
            return portBindings.keySet().iterator().next().getPort();
        }

        return null;
    }

    private InetAddress getIp(InspectContainerResponse info, String network) {
        if (!info.getNetworkSettings().getNetworks().containsKey(network)) {
            return null;
        }

        try {
            return InetAddress.getByName(info.getNetworkSettings().getNetworks().get(network).getIpAddress());
        } catch (UnknownHostException e) {
            return null;
        }
    }

    private Map<String, String> getEnvironmentVariables(InspectContainerResponse info) {
        String[] unformattedArray =  info.getConfig().getEnv();

        if(unformattedArray == null || unformattedArray.length == 0) {
            return  new HashMap<>(0);
        }

        Map<String, String> formattedMap = new HashMap<>(unformattedArray.length);

        for (String environmentVariable: unformattedArray) {
            String[] parts = environmentVariable.split("=");
            if (parts.length == 2) {
                formattedMap.put(parts[0], parts[1]);
            }
        }
        return formattedMap;
    }
}
