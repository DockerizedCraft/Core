package de.craftmania.dockerized_craft.container_management.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import net.md_5.bungee.config.Configuration;

public class DockerClientFactory {

    public static DockerClient getByConfiguration(Configuration configuration) {
        return DockerClientFactory.get(
                configuration.getString("docker.host"),
                configuration.getBoolean("docker.tsl_verify"),
                configuration.getString("docker.cert_path"),
                configuration.getString("docker.registry_username"),
                configuration.getString("docker.registry_password"),
                configuration.getString("docker.registry_email"),
                configuration.getString("docker.registry_url")
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
