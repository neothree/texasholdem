package com.texasthree.appadmin.operator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: neo
 * @create: 2022-09-27 18:16
 */
@Service
public class OperatorService {

    private final OperatorDao dao;

    @Autowired
    public OperatorService(OperatorDao dao) {
        this.dao = dao;
    }

    public Operator getDataByUsername(String username) {
        return this.dao.findByUsername(username).orElse(null);
    }
}
