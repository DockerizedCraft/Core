package de.craftmania.dockerizedcraft.container.inspector;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;

import java.util.logging.Logger;

public class ContainerInspector {
    private ProxyServer proxyServer;
    private Logger logger;

    public ContainerInspector(Configuration configuration, ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
    }

    public void runContainerInspection() {
        //Not running initial inspection, listener does that
    }

    public void runContainerListener() {
        this.logger.info("[Container Inspector] Running listener.");
        KubernetesClient client = new DefaultKubernetesClient();
        client.pods().inNamespace("che").withLabel("dockerizedcraft/enabled=true").watch(new PodWatcher(proxyServer, logger));
    }
}
