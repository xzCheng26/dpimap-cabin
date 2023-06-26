package com.dpi.map.cabin.service;

import com.dpi.map.cabin.common.CommonResult;

import java.math.BigDecimal;

/**
 * @Author: chengxirui
 * @Date: 2023-04-28  08:46
 */
public interface DpiHdvtTileService {
    CommonResult queryPlain(BigDecimal tileX, BigDecimal tileY, BigDecimal tileZ, String serverId, String taskId, String label, String layers) throws InterruptedException;

}
