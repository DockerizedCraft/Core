package de.craftmania.dockerizedcraft.container.inspector.kubernetes;
import net.md_5.bungee.api.ProxyServer;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Watcher;
import de.craftmania.dockerizedcraft.container.inspector.events.ContainerEvent;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.api.model.EnvVar;
import java.util.logging.Logger;
import java.net.InetAddress;
import java.util.*;

public class PodWatcher implements Watcher<Pod> {

    private ProxyServer proxyServer;
    private Logger logger;
    PodWatcher(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
    }

    @Override
    public void eventReceived(Action action, Pod resource) {
        logger.info(action.name());
        logger.info(resource.getStatus().getPhase());
        logger.info(resource.getStatus().getPodIP());
        logger.info(resource.getMetadata().getName());
        logger.info(resource.getSpec().getContainers().get(0).getEnv().toString());
        logger.info(resource.getMetadata().getLabels().toString());

        try {
            ContainerEvent containerEvent = new ContainerEvent(resource.getMetadata().getName(), action.name());
            containerEvent.setName(resource.getMetadata().getName());
            Map<String ,String > environmentVariables = new HashMap<>();
            for (EnvVar i : resource.getSpec().getContainers().get(0).getEnv()) environmentVariables.put(i.getName(),i.getValue());
            containerEvent.setEnvironmentVariables(environmentVariables);
            containerEvent.setPort(Integer.parseInt(environmentVariables.get("SERVER_PORT")));
            containerEvent.setIp(InetAddress.getByName(resource.getStatus().getPodIP()));
            this.proxyServer.getPluginManager().callEvent(containerEvent);
        }catch(java.net.UnknownHostException ex){
            logger.severe(ex.getMessage());
        }

    }

    @Override
    public void onClose(KubernetesClientException cause) {
        logger.warning("Watcher close due to " + cause);
    }
}
