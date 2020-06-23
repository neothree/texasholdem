package com.texasthree.core.service.impl;

import com.texasthree.core.service.UniqueIDGeneratorService;
import com.texasthree.core.util.NadronConfig;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;


/**
 * Uses an atomic long to increment and provide a unique id. This will not work
 * in case of clustered servers.
 *
 * @author Abraham.Menacherry
 */
@Service
public class SimpleUniqueIdGenerator implements UniqueIDGeneratorService {
    public static final AtomicLong ID = new AtomicLong(0l);

    @Override
    public Object generate() {
        String nodeName = System.getProperty(NadronConfig.NODE_NAME);
        if (null == nodeName || "".equals(nodeName)) {
            return ID.incrementAndGet();
        } else {
            return nodeName + ID.incrementAndGet();
        }
    }

    @Override
    public Object generateFor(@SuppressWarnings("rawtypes") Class klass) {
        return klass.getSimpleName() + ID.incrementAndGet();
    }

}
