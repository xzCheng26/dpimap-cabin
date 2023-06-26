package com.dpi.map.cabin.domain.pointcloud;

import lombok.Data;

@Data
public class ObsField {

    private String name;

    private Integer offset;

    private Integer datatype;

    private Integer count;
}
