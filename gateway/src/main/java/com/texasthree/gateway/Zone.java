package com.texasthree.gateway;

import lombok.Data;

/**
 * @author: neo
 * @create: 2021-07-20 17:06
 */
@Data
public class Zone {
    public String id;

    public String name;

    @Override
    public String toString() {
        return "[" + id + ":" + name + "]";
    }
}
