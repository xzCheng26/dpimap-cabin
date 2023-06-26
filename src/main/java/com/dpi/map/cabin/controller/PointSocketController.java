package com.dpi.map.cabin.controller;

import com.alibaba.fastjson.JSONObject;
import com.dpi.map.cabin.server.pointcloud.PointCloundWebSocket;
import com.dpi.map.cabin.server.pointcloud.PointObsWebSocket;
import lombok.extern.slf4j.Slf4j;
import org.opengis.referencing.FactoryException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@Slf4j
@RequestMapping("/cabin-point")
public class PointSocketController {

    @GetMapping(value = "/obs")
    public String pushAllObstacle(@RequestParam("cell") Integer cell) throws IOException, FactoryException {

        String fileName = File.separator + "opt" + File.separator + "points_cluster.log";
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName), StandardCharsets.UTF_8)) {
            for (String line = null; (line = br.readLine()) != null;) {
                if (line.endsWith("---")) {
                    Yaml yaml = new Yaml();
                    String jsonstr = JSONObject.toJSONString(yaml.load(sb.toString()));
                    PointObsWebSocket.sendInfoToClient(jsonstr);
                    if (cell != null) {
                        try {
                            Thread.sleep(cell);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    sb.setLength(0);

                } else {
                    sb.append(line).append("\n");
                }
            }
        }
//        List<PointCloudObsResponse> obstacleResponseList = Lists.newArrayList();
//        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);) {
//            String json = IOUtils.toString(is, "utf-8");
//            String[] jsons = json.split("---");
//
//            for (String str : jsons) {
//                Yaml yaml = new Yaml();
//                String jsonstr = JSONObject.toJSONString(yaml.load(str));
//
//
//                JSONObject jsonObject = JSONObject.parseObject(jsonstr);
//                JSONArray jsonArray = jsonObject.getJSONArray("fields");
//                List<ObsField> obsFieldList = JSONArray.parseArray(jsonArray.toJSONString(), ObsField.class);
//
//                ObsHeader obsHeader = jsonObject.getObject("header", ObsHeader.class);
//
//                PointCloudObsResponse pointCloudObsResponse = new PointCloudObsResponse();
//                pointCloudObsResponse.setHeader(obsHeader);
//                pointCloudObsResponse.setHeight(jsonObject.getInteger("height"));
//                pointCloudObsResponse.setWidth(jsonObject.getInteger("width"));
//                pointCloudObsResponse.setFields(obsFieldList);
//                pointCloudObsResponse.setIsBigendian(jsonObject.getBoolean("is_bigendian"));
//                pointCloudObsResponse.setPointStep(jsonObject.getInteger("point_step"));
//                pointCloudObsResponse.setRowStep(jsonObject.getInteger("row_step"));
//                Integer[] data = jsonObject.getObject("data", Integer[].class);
//                pointCloudObsResponse.setData(data);
//                pointCloudObsResponse.setIsDense(jsonObject.getBoolean("is_dense"));
//                obstacleResponseList.add(pointCloudObsResponse);
//                System.out.println(JSONUtil.toJsonStr(pointCloudObsResponse));
////                ObstacleWebSocket.sendInfoToClient(JSONUtil.toJsonStr(obstacleResponseList));
////                if (cell != null) {
////                    try {
////                        Thread.sleep(cell);
////                    } catch (InterruptedException e) {
////                        throw new RuntimeException(e);
////                    }
////                }
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        return "push msg: success";
    }

    @GetMapping(value = "/ground")
    public String pushAllGround(@RequestParam("cell") Integer cell) throws IOException {

        String fileName = File.separator + "opt" + File.separator + "ground_cloud.log";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName), StandardCharsets.UTF_8)) {
            for (String line = null; (line = br.readLine()) != null;) {
                if (line.endsWith("---")) {
                    Yaml yaml = new Yaml();
                    String jsonstr = JSONObject.toJSONString(yaml.load(sb.toString()));
                    PointCloundWebSocket.sendInfoToClient(jsonstr);
                    if (cell != null) {
                        try {
                            Thread.sleep(cell);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    sb.setLength(0);

                } else {
                    sb.append(line).append("\n");
                }
            }
        }
        return "push msg: success";
    }
}
