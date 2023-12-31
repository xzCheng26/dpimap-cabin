package com.dpi.map.cabin.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 实时位置
 * @Author: chengxirui
 * @Date: 2023-05-05  10:23
 */
@Data
public class PositionResponse {



    private Double positionX;

    private Double positionY;

    /**
     * 经度
     */
    private Double longitude;
    /**
     * 纬度
     */
    private Double latitude;
    /**
     * 高度，单位：m
     */
    private Double altitude;
    /**
     * 航向角,单位：du
     */
    private Float yaw;

    private Double speedX;

    private Double speedY;

    private String currentTime;

    private Float pitch;

    private Float roll;

    private Float azimuth;

    private List<Double[]> data;

}
