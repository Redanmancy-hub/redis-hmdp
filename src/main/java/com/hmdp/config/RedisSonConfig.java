package com.hmdp.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisSonConfig {
    @Bean
    public RedissonClient redissonClient(){
        //配置类
        Config config = new Config();
        // 添加redis地址，这里添加了单节点地址，以可以使用config.useClusterServers()添加集群地址
        config.useSingleServer().setAddress("redis://192.168.138.128:6379").setPassword("123456");
        //创建客服端
        return Redisson.create(config);
    }
}
