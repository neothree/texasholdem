package com.texasthree.room;

import com.texasthree.core.service.LookupService;
import com.texasthree.core.service.impl.SimpleLookupService;
import org.msgpack.MessagePack;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * create at:  2020-07-01  18:21
 *
 * @author : neo
 */
@Configuration
public class ServerConfig {
    @Bean
    public LookupService lookupService() {
        return new SimpleLookupService();
    }

}
