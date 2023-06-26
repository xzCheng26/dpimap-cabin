package com.dpi.map.cabin.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 周边障碍
 * @Author: chengxirui
 * @Date: 2023-04-27  10:51
 */
@Data
public class ObstacleRequest {
    private String type;
    private BigDecimal[] position;
    private BigDecimal[] direction;
}
