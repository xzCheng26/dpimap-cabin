package com.dpi.map.cabin.domain;

import lombok.Data;

/**
 * @Author: chengxirui
 * @Date: 2023-05-05  10:42
 */
@Data
public class ObstacleOfflineResponse {

    /**
     * 重叠距离,m
     */
    private Double overlapDis;
    /**
     *显示距离，m
     */
    private Double showDis;
    /**
     * 经度
     */
    private Double longitude;
    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 航向,单位：du
     */
    private Double heading;

    /**
     * 类型
     * CAR_TYPE=0, TRUCK_TYPE=1, PEDESTRIAN_TYPE=2, CYCLIST_TYPE=3, UNKNOWN_TYPE=4, UNKNOWN_MOVABLE_TYPE=5, UNKNOWN_UNMOVABLE_TYPE=6
     */
    private Integer type;

}
