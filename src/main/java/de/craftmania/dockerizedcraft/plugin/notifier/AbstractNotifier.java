package de.craftmania.dockerizedcraft.plugin.notifier;

import net.md_5.bungee.api.config.ServerInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class AbstractNotifier {
    @SuppressWarnings("SameParameterValue")
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
