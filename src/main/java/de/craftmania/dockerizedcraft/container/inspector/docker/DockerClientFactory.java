package de.craftmania.dockerizedcraft.container.inspector.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import net.md_5.bungee.config.Configuration;

class DockerClientFactory {

    static DockerClient getByConfiguration(Configuration configuration) {
        return DockerClientFactory.get(
                configuration.getString("docker.host"),
                configuration.getBoolean("docker.tsl-verify"),
                configuration.getString("docker.cert-path"),
                configuration.getString("docker.registry.username"),
                configuration.getString("docker.registry.password"),
                configuration.getString("docker.registry.email"),
                configuration.getString("docker.registry.url")
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