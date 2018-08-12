package de.craftmania.dockerizedcraft.container.inspector.client;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import net.md_5.bungee.config.Configuration;

public class DockerClientFactory {

    public static DockerClient getByConfiguration(Configuration configuration) {
        return DockerClientFactory.get(
                configuration.getString("host"),
                configuration.getBoolean("tsl-verify"),
                configuration.getString("cert-path"),
                configuration.getString("registry.username"),
                configuration.getString("registry.password"),
                configuration.getString("registry.email"),
                configuration.getString("registry.url")
        );
    }

    private static DockerClient get(
            String host,
            Boolean tlsVerify,
            String certPath,
            String registryUsername,
            String registryPass,
            String registryMail,
            String registryUrl
    ) {
        DefaultDockerClientConfig.Builder configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(host);

        configBuilder.withDockerTlsVerify(tlsVerify);

        if (!certPath.equals("")) {
            configBuilder.withDockerCertPath(certPath);
        }

        if(!registryUrl.equals("")) {
            configBuilder.withRegistryUrl(registryUrl);
            if (!registryUsername.equals("")) {
                configBuilder.withRegistryUsername(registryUsername);
            }

            if (!registryMail.equals("")) {
                configBuilder.withRegistryEmail(registryMail);
            }

            if (!registryPass.equals("")) {
                configBuilder.withRegistryPassword(registryPass);
            }
        }

        DockerClientConfig config = configBuilder.build();

        return DockerClientBuilder.getInstance(config).build();
    }
}
