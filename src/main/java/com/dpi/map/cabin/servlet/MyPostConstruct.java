package com.dpi.map.cabin.servlet;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dpi.map.cabin.common.Const;
import com.dpi.map.cabin.domain.ObstacleOfflineResponse;
import com.dpi.map.cabin.server.*;
import com.dpi.map.cabin.server.render.CollectDetailWebSocket;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class MyPostConstruct {

    @Autowired
    private Cache<String, Object> cache;

    @javax.annotation.PostConstruct
    public void initTcpServer() {
                Long cutTime = System.currentTimeMillis();
//                File file = new File(Const.LOG_PATH + "\\" + cutTime);
//                if (!file.exists()){
//                    file.mkdirs();
//                }
                cache.put("logFilePath", cutTime);

                TCPServer positionServer = new TCPServer(30000);
                new Thread(()-> {
                    positionServer.start();
                }).start();
                System.out.println("实时位置服务启动");

                TCPServer obsServer = new TCPServer(30001);
                new Thread(()-> {
                    obsServer.start();
                }).start();
                System.out.println("障碍物服务启动");

                TCPServer lineServer = new TCPServer(30002);
                new Thread(()-> {
                    lineServer.start();
                }).start();
                System.out.println("引导线服务启动");

                TCPServer speedServer = new TCPServer(30003);
                new Thread(() -> {
                    cache.put("speed", 0);
                    speedServer.start();
                    })
                .start();
                System.out.println("车速服务启动");

                initCollectServer();
//
    }

    private void initCollectServer() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://mep2-dpi-gateway-external.uat.dpi-earth.cn:18010/gisserver/external/query-new";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Cache<String, Object> cache = BeanContext.getBean(Cache.class);
        Map<String, Object> map = Maps.newHashMap();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    Runnable task =
        () -> {
          try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
              String responseBody = response.body().string();
              // 处理响应数据
              JSONObject jsonObject = JSONObject.parseObject(responseBody);
              JSONObject dataObj = jsonObject.getJSONObject("data");
              Integer taskId = dataObj.getInteger("taskId");
              String handleVehicle = dataObj.getString("handleVehicle");
              String taskStatus = dataObj.getString("taskStatus");
              String collectRoad = dataObj.getString("collectRoad");
              Object taskIdObj = cache.getIfPresent("taskId");
              if (taskIdObj == null) {
                  cache.put("taskId", taskId);
                  map.put("taskId", taskId);
                  map.put("handleVehicle", handleVehicle);
                  map.put("taskStatus", taskStatus);
                  map.put("collectRoad", collectRoad);
                  System.out.println(JSONUtil.toJsonStr(map));
                  CollectDetailWebSocket.sendInfoToClient(JSONUtil.toJsonStr(map));
              }
            } else {
              // 处理错误响应
              System.out.println("请求失败：" + response.code() + " " + response.message());
            }
          } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
          }
        };

        // 设置定时任务的延迟时间和执行间隔时间（单位：毫秒）,从当前时间开始，每隔1秒执行一次
        executor.scheduleAtFixedRate(task, 0, 1000, TimeUnit.MILLISECONDS);
//        executor.shutdown(); // 关闭定时任务
    }
}
