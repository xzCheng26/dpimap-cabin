package com.dpi.map.cabin.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dpi.map.cabin.common.CodeEnum;
import com.dpi.map.cabin.common.CommonException;
import com.dpi.map.cabin.common.CommonResult;
import com.dpi.map.cabin.common.Const;
import com.dpi.map.cabin.domain.GuideLineResponse;
import com.dpi.map.cabin.domain.ObstacleOfflineResponse;
import com.dpi.map.cabin.domain.ObstacleResponse;
import com.dpi.map.cabin.domain.PositionResponse;
import com.dpi.map.cabin.server.GuidelineWebSocket;
import com.dpi.map.cabin.server.ObstacleWebSocket;
import com.dpi.map.cabin.server.PositionWebSocket;
import com.dpi.map.cabin.service.DpiHdvtTileService;
import com.dpi.map.cabin.service.WebSocketService;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Random;


/**
 * @Author: chengxirui
 * @Date: 2023-04-25  15:14
 */
@RestController
@Slf4j
@RequestMapping("/cabin")
public class WebSocketController {

    @Autowired
    private DpiHdvtTileService dpiHdvtTileService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    Cache<String, Object> obsCache;

    @GetMapping(value = "/getCache")
    public CommonResult getCache() throws InterruptedException {
        Object obj = obsCache.getIfPresent("offlineObs");
        List<ObstacleOfflineResponse> list = (List<ObstacleOfflineResponse>) obj;
        return CommonResult.ok(list);
    }

    /**
     * 不加密版本.
     */
    @GetMapping(value = "/queryPlain")
    public CommonResult queryPlain(@RequestParam(value ="tileX", required = false) BigDecimal tileX,
                                   @RequestParam(value ="tileY", required = false) BigDecimal tileY,
                                   @RequestParam(value ="tileZ", required = false) BigDecimal tileZ,
                                   @RequestParam(value ="serverId") String serverId,
                                   @RequestParam(value ="taskId", required = false) String taskId,
                                   @RequestParam(value ="label", required = false) String label,
                                   @RequestParam(value = "layers", required = false) String layers) throws InterruptedException {
        this.checkObjectNotNull(serverId, "serverId");
        return dpiHdvtTileService.queryPlain(tileX, tileY, tileZ, serverId, taskId, label, layers);
    }

  @GetMapping(value = "/push-all")
  public String pushAllMessage(@RequestParam("cell") Integer cell) throws FactoryException, IOException {
        return webSocketService.pushAllMessage(cell);
    }

    /**
     * 批量-周边障碍物
     */
    @GetMapping(value = "/batch-add-obs")
    public String pushAllObstacle(@RequestParam("cell") Integer cell) throws IOException, FactoryException {

        String utmCode = String.format("EPSG:%d%s", 32600 + Const.ZONE_NUMBER, Const.IS_NORTHERN_HEMISPHERE ? "" : "+south");
        CoordinateReferenceSystem utmCrs = CRS.decode(utmCode);
        // 获取WGS84投影的CoordinateReferenceSystem
        CoordinateReferenceSystem wgs84Crs = CRS.decode("EPSG:4326");

        String fileName = "doc/obs.txt";
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);) {
            String json = IOUtils.toString(is, "utf-8");
            String[] jsons = json.split("---");
            for (String str : jsons) {
                Yaml yaml = new Yaml();
                String jsonstr = JSONObject.toJSONString(yaml.load(str));

                JSONObject jsonObject = JSONObject.parseObject(jsonstr);
                JSONArray jsonArray = jsonObject.getJSONArray("objs");
                List<ObstacleResponse> obstacleResponseList = Lists.newArrayList();
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
        }

        return "push msg: success";
    }

    /**
     * 实时位置
     */
    @GetMapping(value = "/add-position")
    public String pushPosition(@RequestParam("cell") Integer cell) throws IOException {
        Object obj = obsCache.getIfPresent("offlineObs");
        String fileName = "doc/position.txt";
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);) {
            String json = IOUtils.toString(is, "utf-8");
            String[] jsons = json.split("---");
            for (String str : jsons) {
                //yaml转json
                Yaml yaml = new Yaml();
                String jsonstr = JSONObject.toJSONString(yaml.load(str));
                JSONObject local = JSONObject.parseObject(jsonstr);
                PositionResponse positionResponse = new PositionResponse();
                positionResponse.setPositionX(local.getDouble("position_x"));
                positionResponse.setPositionY(local.getDouble("position_y"));
                positionResponse.setAltitude(local.getDouble("altitude"));
                positionResponse.setYaw(local.getDouble("yaw"));

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
        }
        return "push msg: " + "success!";
    }

    /**
     * 引导线
     */
    @GetMapping(value = "/batch-add-guideline")
    public String pushAllGuideline(@RequestParam("cell") Integer cell) throws FactoryException {
        String utmCode = String.format("EPSG:%d%s", 32600 + Const.ZONE_NUMBER, Const.IS_NORTHERN_HEMISPHERE ? "" : "+south");
        CoordinateReferenceSystem utmCrs = CRS.decode(utmCode);
        // 获取WGS84投影的CoordinateReferenceSystem
        CoordinateReferenceSystem wgs84Crs = CRS.decode("EPSG:4326");

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
        }

        return "push msg: success";
    }


    /**
     * 批量-引导线
     */
    @GetMapping(value = "/batch-add-guideline-2")
    public String pushAllGuideline2(@RequestParam("cell") Integer cell) {

        String fileName = "doc/track_demo_10.json";
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);) {
            String json = IOUtils.toString(is, "utf-8");
            JSONObject object = JSONObject.parseObject(json);
            JSONArray array= JSON.parseArray(object.getString("guideline"));
            array.stream().filter(Objects::nonNull).forEach(x->{
                if (cell != null) {
                    try {
                        Thread.sleep(cell);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                String coordinates = JSONUtil.toJsonStr(x);
                String[] aa = coordinates.split("],");
                PositionResponse positionResponse = new PositionResponse();
                positionResponse.setPositionX((double) 0);
                positionResponse.setPositionY((double) 0);

                Random words = new Random();
                float[] heading ={15, 15.2f,30.5f,30,45,45.8f,60,90,180,210};
                int hIndex = words.nextInt(heading.length);
                positionResponse.setAltitude(1.2);
                positionResponse.setYaw((double) heading[hIndex]);


                for (String bb : aa) {
                    String[] vv = bb.replace("[", "").split(",");
                    System.out.println(vv);
                    Float longitude = Float.valueOf(vv[0]) + 0.0001f;
                    positionResponse.setLongitude(Double.valueOf(longitude));
                    Float latitude = Float.valueOf(vv[1]) + 0.0001f;
                    positionResponse.setLatitude(Double.valueOf(latitude));
                }
                try {
                    GuidelineWebSocket.sendInfoToClient(JSONUtil.toJsonStr(x));
                    PositionWebSocket.sendInfoToClient(JSONUtil.toJsonStr(positionResponse));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            System.out.println(fileName + "文件读取异常" + e);
        }

        return "push msg: success";
    }

    private void checkObjectNotNull(Object obj, String message) {
        if (obj == null) {
            throw new CommonException(CodeEnum.SYSTEM_NO_SUCH_PARAMENT_ERROR, message);
        }
    }

}