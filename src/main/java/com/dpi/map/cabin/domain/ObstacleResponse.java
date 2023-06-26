package com.dpi.map.cabin.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Author: chengxirui
 * @Date: 2023-05-05  10:42
 */
@Data
public class ObstacleResponse {

    /**
     * 经度
     */
    private Double longitude;
    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 经度, x的相对距离
     */
    private Double xabs;
    /**
     * 纬度, y的相对距离
     */
    private Double yabs;


    /**
     * 航向,单位：du
     */
    private Double heading;

    /**
     * 类型
     * CAR_TYPE=0, TRUCK_TYPE=1, PEDESTRIAN_TYPE=2, CYCLIST_TYPE=3, UNKNOWN_TYPE=4, UNKNOWN_MOVABLE_TYPE=5, UNKNOWN_UNMOVABLE_TYPE=6
     */
    private Integer type;

    private String currentTime;

}
