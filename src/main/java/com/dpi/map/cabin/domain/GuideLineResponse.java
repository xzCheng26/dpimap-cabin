package com.dpi.map.cabin.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Author: chengxirui
 * @Date: 2023-05-06  17:00
 */
@Data
public class GuideLineResponse {
    /**
     * 经度
     */
    private Double longitude;
    /**
     * 纬度
     */
    private Double latitude;

    private String currentTime;
}
