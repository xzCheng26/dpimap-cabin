package com.dpi.map.cabin.service;

import org.opengis.referencing.FactoryException;

import java.io.IOException;

/**
 * @Author: chengxirui
 * @Date: 2023-05-06  11:03
 */
public interface WebSocketService {
    String pushAllMessage(Integer cell) throws FactoryException, IOException;
}
