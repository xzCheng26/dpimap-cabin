package com.dpi.map.cabin.service.impl;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dpi.map.cabin.common.Const;
import com.dpi.map.cabin.domain.GuideLineResponse;
import com.dpi.map.cabin.domain.ObstacleResponse;
import com.dpi.map.cabin.domain.PositionResponse;
import com.dpi.map.cabin.server.GuidelineWebSocket;
import com.dpi.map.cabin.server.ObstacleWebSocket;
import com.dpi.map.cabin.server.PositionWebSocket;
import com.dpi.map.cabin.service.WebSocketService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * @Author: chengxirui
 * @Date: 2023-05-06  11:03
 */
@Slf4j
@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Override
    public String pushAllMessage(Integer cell) throws FactoryException {

        String utmCode = String.format("EPSG:%d%s", 32600 + Const.ZONE_NUMBER, Const.IS_NORTHERN_HEMISPHERE ? "" : "+south");
        CoordinateReferenceSystem utmCrs = CRS.decode(utmCode);
        // 获取WGS84投影的CoordinateReferenceSystem
        CoordinateReferenceSystem wgs84Crs = CRS.decode("EPSG:4326");
        Thread thread1 = new Thread(() -> {
            // 引导线
            buildGuideLine(utmCrs, wgs84Crs, cell);
        });
        thread1.start();

        Thread thread2 = new Thread(() -> {
            //实时位置
            buildPosition(cell);
        });
        thread2.start();

        Thread thread3 = new Thread(() -> {
            // 障碍物
            buildObs(utmCrs, wgs84Crs, cell);
        });
        thread3.start();

        return "success";
    }

    private  List<ObstacleResponse> buildObs(CoordinateReferenceSystem utmCrs, CoordinateReferenceSystem wgs84Crs, Integer cell) {
        String fileName = "doc/obs.txt";
        List<ObstacleResponse> obstacleResponseList = Lists.newArrayList();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);) {
            String json = IOUtils.toString(is, "utf-8");
            String[] jsons = json.split("---");
            for (String str : jsons) {
                Yaml yaml = new Yaml();
                String jsonstr = JSONObject.toJSONString(yaml.load(str));

                JSONObject jsonObject = JSONObject.parseObject(jsonstr);
                JSONArray jsonArray = jsonObject.getJSONArray("objs");

                for (int i = 0; i < jsonArray.size() ; i++) {
                    JSONObject local = jsonArray.getJSONObject(i);
                    ObstacleResponse obstacleResponse = new ObstacleResponse();

                    // 创建坐标转换
                    MathTransform transform = CRS.findMathTransform(utmCrs, wgs84Crs);
                    // 创建UTM坐标
                    DirectPosition2D utmCoord = new DirectPosition2D(utmCrs, local.getDouble("xabs"), local.getDouble("yabs"));
                    // 将UTM坐标转换为WGS84坐标
                    DirectPosition2D wgs84Coord = new DirectPosition2D();
                    transform.transform(utmCoord, wgs84Coord);

                    obstacleResponse.setLongitude(wgs84Coord.y);
                    obstacleResponse.setLatitude(wgs84Coord.x);

                    obstacleResponse.setXabs(local.getDouble("xabs"));
                    obstacleResponse.setYabs(local.getDouble("yabs"));
                    obstacleResponse.setHeading(local.getDouble("heading"));
                    obstacleResponse.setType(local.getInteger("type"));
                    obstacleResponseList.add(obstacleResponse);
                }
                ObstacleWebSocket.sendInfoToClient(JSONUtil.toJsonStr(obstacleResponseList));
                if (cell != null) {
                    try {
                        Thread.sleep(cell);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        } catch (TransformException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (FactoryException e) {
            throw new RuntimeException(e);
        }
        return obstacleResponseList;
    }

    private void buildPosition(Integer cell) {
        String fileName = "doc/position.txt";
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);) {
            String json = IOUtils.toString(is, "utf-8");
            String[] jsons = json.split("---");
            List<PositionResponse> positionResponseList = Lists.newArrayList();
            for (String str : jsons) {
                //yaml转json
                Yaml yaml = new Yaml();
                String jsonstr = JSONObject.toJSONString(yaml.load(str));
                JSONObject local = JSONObject.parseObject(jsonstr);
                PositionResponse positionResponse = new PositionResponse();
                positionResponse.setPositionX(local.getDouble("position_x"));
                positionResponse.setPositionY(local.getDouble("position_y"));
                positionResponse.setAltitude(local.getDouble("altitude"));
                positionResponse.setYaw(local.getFloat("yaw"));

                positionResponse.setLongitude(local.getDouble("longitude"));
                positionResponse.setLatitude(local.getDouble("latitude"));
                PositionWebSocket.sendInfoToClient(JSONUtil.toJsonStr(positionResponse));
                if (cell != null) {
                    try {
                        Thread.sleep(cell);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void buildGuideLine(CoordinateReferenceSystem utmCrs, CoordinateReferenceSystem wgs84Crs, Integer cell) {
        String fileName = "doc/guideline.txt";

        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);) {
            String json = IOUtils.toString(is, "utf-8");
            String[] jsons = json.split("---");
            for (String str : jsons) {
                Yaml yaml = new Yaml();
                String jsonstr = JSONObject.toJSONString(yaml.load(str));

                JSONObject jsonObject = JSONObject.parseObject(jsonstr);
                JSONObject tra = (JSONObject) jsonObject.get("trajectoryinfo");
                JSONArray jsonArray = tra.getJSONArray("trajectorypoints");
                List<GuideLineResponse> guideLineResponseArrayList = Lists.newArrayList();
                for (int i = 0; i < jsonArray.size() ; i++) {
                    JSONObject local = jsonArray.getJSONObject(i);
                    GuideLineResponse guideLineResponse = new GuideLineResponse();

                    // 创建坐标转换
                    MathTransform transform = CRS.findMathTransform(utmCrs, wgs84Crs);
                    // 创建UTM坐标
                    JSONObject position = (JSONObject) local.get("position");
                    DirectPosition2D utmCoord = new DirectPosition2D(utmCrs, position.getDouble("x"), position.getDouble("y"));
                    // 将UTM坐标转换为WGS84坐标
                    DirectPosition2D wgs84Coord = new DirectPosition2D();
                    transform.transform(utmCoord, wgs84Coord);

                    guideLineResponse.setLongitude(wgs84Coord.y);
                    guideLineResponse.setLatitude(wgs84Coord.x);

                    guideLineResponseArrayList.add(guideLineResponse);
                }
                GuidelineWebSocket.sendInfoToClient(JSONUtil.toJsonStr(guideLineResponseArrayList));
            }
        } catch (TransformException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (FactoryException e) {
            throw new RuntimeException(e);
        }
    }
}
