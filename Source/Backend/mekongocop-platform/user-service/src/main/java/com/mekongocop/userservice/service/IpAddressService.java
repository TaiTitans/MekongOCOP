package com.mekongocop.userservice.service;
import com.mekongocop.userservice.common.IpAddressInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class IpAddressService {

    private static final String IP_ADDRESS_KEY_PREFIX = "ip_address:";
    private static final int IP_ADDRESS_EXPIRATION_HOURS = 1;

    private final JedisPool jedisPool;

    @Autowired
    public IpAddressService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void saveIpAddressInfo(String ipAddress, int sendCount, String lastSendTime) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = IP_ADDRESS_KEY_PREFIX + ipAddress;
            jedis.hset(key, "ipAddress", ipAddress);
            jedis.hset(key, "sendCount", String.valueOf(sendCount));
            jedis.hset(key, "lastSendTime", lastSendTime);
            jedis.expire(key, IP_ADDRESS_EXPIRATION_HOURS * 3600); // Expire in 1 hour
        }
    }

    public IpAddressInfo getIpAddressInfo(String ipAddress) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = IP_ADDRESS_KEY_PREFIX + ipAddress;
            if (jedis.exists(key)) {
                String ipAddressValue = jedis.hget(key, "ipAddress");
                int sendCount = Integer.parseInt(jedis.hget(key, "sendCount"));
                String lastSendTime = jedis.hget(key, "lastSendTime");
                return new IpAddressInfo(ipAddressValue, sendCount, lastSendTime);
            }
            return null;
        }
    }
}