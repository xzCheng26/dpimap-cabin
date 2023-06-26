package com.dpi.map.cabin.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 引导线
 * @Author: chengxirui
 * @Date: 2023-04-27  10:59
 */
@Data
public class GuideLineRequest {
    private BigDecimal[] position;
}
