package com.dpi.map.cabin.domain.pointcloud;

import lombok.Data;

import java.util.List;

@Data
public class PointCloudObsResponse {

    private ObsHeader header;

    private Integer height;

    private Integer width;

    private List<ObsField> fields;

    private Boolean isBigendian;

    private Integer pointStep;

    private Integer rowStep;

    private Integer[] data;

    private Boolean isDense;

}
