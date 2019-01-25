package de.craftmania.dockerizedcraft.container.inspector.kubernetes;
import de.craftmania.dockerizedcraft.container.inspector.IContainerInspector;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;

import java.util.logging.Logger;

public class KubernetesContainerInspector implements IContainerInspector {
    private ProxyServer proxyServer;
    private Logger logger;
    private Configuration configuration;
    private KubernetesClient client;

    public KubernetesContainerInspector(Configuration configuration, ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.configuration = configuration;
    }

    public void runContainerInspection() {
        this.logger.info("[Kubernetes Container Inspector] Connecting to kubernetes.");
        client = new DefaultKubernetesClient();
    }

    public void runContainerListener() {
        this.logger.info("[Kubernetes Container Inspector] Running listener.");
        client.pods().inNamespace(configuration.getString("kubernetes.namespace")).withLabel("dockerizedcraft/enabled=true").watch(new PodWatcher(proxyServer, logger));
    }
}
