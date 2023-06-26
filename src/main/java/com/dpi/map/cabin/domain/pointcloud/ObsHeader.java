package com.dpi.map.cabin.domain.pointcloud;

import lombok.Data;

@Data
public class ObsHeader {

    private Integer seq;

    private HeaderStamp stamp;

    private String frameId;
}
