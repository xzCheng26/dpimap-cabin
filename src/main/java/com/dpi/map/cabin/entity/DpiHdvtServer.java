package com.dpi.map.cabin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dpi_hdvt_server")
public class DpiHdvtServer {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name;

    private String url;

    private Integer status;

    private String layerName;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @TableField(fill = FieldFill.INSERT)
    private Date updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long updateUserId;

    private Integer stopFlag;

    private String taskId;

    private Integer progress;

    private String label;

    private String serverId;

    private String message;

    private String sourceDataName;

    private String dataSources;
}
