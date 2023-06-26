package com.dpi.map.cabin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author DPI270
 * @Date 2022/9/19 10:37
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dpi_tile")
public class DpiTile {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private BigDecimal tileX;

    private BigDecimal tileY;

    private BigDecimal tileZ;

    private String label;

    private String layerName;

    private String tileData;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @TableField(fill = FieldFill.INSERT)
    private Date updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long updateUserId;

    private Integer stopFlag;

    private String tileUuid;

    private String taskId;

    private String serverId;

    private String layerId;
}
