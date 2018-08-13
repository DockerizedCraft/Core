package de.craftmania.dockerizedcraft.connection.balancer.session;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.UUID;

public class RedisSessionStorage implements SessionStorage {

    static private String prefix;

    static {
        prefix = "player/session/";
    }

    private JedisPool jedisPool;
    private String password;

    public RedisSessionStorage(String hostname, String password, Integer port, Boolean ssl) {
        this.jedisPool = new JedisPool(hostname, port);
        this.password = password;
    }

    @Override
    public void setReconnectServer(UUID uuid, String serverName) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            if (this.password != null && !this.password.equals("")) {
                jedis.auth(this.password);
            }
            jedis.set((RedisSessionStorage.prefix + uuid.toString()), serverName);
        }
    }

    @Override
    public String getReconnectServer(UUID uuid) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            if (this.password != null && !this.password.equals("")) {
                jedis.auth(this.password);
            }
            System.out.print(jedis.get((RedisSessionStorage.prefix + uuid.toString())));
            return jedis.get((RedisSessionStorage.prefix + uuid.toString()));
        } catch (Exception e) {
            return null;
        }
    }
}
