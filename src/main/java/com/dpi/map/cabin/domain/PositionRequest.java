package com.dpi.map.cabin.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 实时位置
 * @Author: chengxirui
 * @Date: 2023-04-27  10:48
 */
@Data
public class PositionRequest {

    private BigDecimal[] position;

    private BigDecimal[] direction;
}
