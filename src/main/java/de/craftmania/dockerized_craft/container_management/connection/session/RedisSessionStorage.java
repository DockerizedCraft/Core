package de.craftmania.dockerized_craft.container_management.connection.session;

import redis.clients.jedis.Jedis;

import java.util.UUID;

public class RedisSessionStorage implements SessionStorage {

    static private String prefix;

    static {
        prefix = "player.session.";
    }

    private Jedis jedis;

    public RedisSessionStorage(String hostname, Integer port, Boolean ssl) {
        this.jedis = new Jedis(hostname, port, ssl);
    }

    @Override
    public void setReconnectServer(UUID uuid, String serverName) {
        this.jedis.set((RedisSessionStorage.prefix + uuid.toString()), serverName);
    }

    @Override
    public String getReconnectServer(UUID uuid) {
        return this.jedis.get((RedisSessionStorage.prefix + uuid.toString()));
    }
}
