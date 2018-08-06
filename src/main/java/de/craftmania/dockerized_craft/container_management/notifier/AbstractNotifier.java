package de.craftmania.dockerized_craft.container_management.notifier;

import net.md_5.bungee.api.config.ServerInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

abstract class AbstractNotifier {
    protected void sendMessage(ServerInfo serverInfo, String channel, String subchannel, String message) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF(subchannel);
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        serverInfo.sendData(channel, stream.toByteArray());
    }
}
