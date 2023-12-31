package com.dpi.map.cabin.servlet;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.dpi.map.cabin.domain.PositionResponse;
import com.dpi.map.cabin.server.*;
import com.dpi.map.cabin.server.pointcloud.PointPositionWebSocket;
import com.dpi.map.cabin.server.render.CollectDetailWebSocket;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MyPostConstruct {

    @Autowired
    private Cache<String, Object> cache;

    @javax.annotation.PostConstruct
    public void initTcpServer() throws IOException {
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

                TCPServer pointPositionServer = new TCPServer(30010);
                new Thread(pointPositionServer::start).start();
                System.out.println("点云实时位置服务启动");

                TCPServer pointCloundServer = new TCPServer(30011);
                new Thread(pointCloundServer::start).start();
                System.out.println("点云服务启动");

                new Thread(new ConsultationReciveSocket(30012, "青勘院实时位置服务启动")).start();

                initCollectServer();
                initShootServer();
                buildPushPositionWebSocket();

    }

    private void buildPushPositionWebSocket() throws IOException {
        Cache<String, Object> cache = BeanContext.getBean(Cache.class);
        PositionResponse positionResponse = new PositionResponse();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        log.info("开始从缓存取定位数据......");
        Runnable task = () -> {
            if (cache.getIfPresent("longitude") != null) {
                positionResponse.setLongitude((Double) cache.getIfPresent("longitude"));
                positionResponse.setLatitude((Double) cache.getIfPresent("latitude"));
                double degrees = Math.toDegrees((float)cache.getIfPresent("yaw"));
                positionResponse.setYaw((float) degrees);
                positionResponse.setCurrentTime(DateUtil.formatDateTime(new Date()));
                positionResponse.setPitch((Float) cache.getIfPresent("pitch"));
                positionResponse.setRoll((Float) cache.getIfPresent("roll"));
                positionResponse.setAzimuth((float) degrees);
                try {
                    PointPositionWebSocket.sendInfoToClient(JSONUtil.toJsonStr(positionResponse));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        // 设置定时任务的延迟时间和执行间隔时间（单位：毫秒）,从当前时间开始，每隔30毫秒执行一次
        executor.scheduleAtFixedRate(task, 0, 30, TimeUnit.MILLISECONDS);
    }

    private void initShootServer() {

        OkHttpClient client = new OkHttpClient();
        String url = "https://mep2-dpi-gateway-external.uat.dpi-earth.cn:18010/gisserver/external/query/hdvt-detail-sid?serverId=f3c106963a2c49da";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Cache<String, Object> cache = BeanContext.getBean(Cache.class);
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(1);
        map.put("sys", "RENDER");
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonObject = JSONObject.parseObject(responseBody);
                    JSONObject dataObj = jsonObject.getJSONObject("data");
                    Integer version = dataObj.getInteger("version");
                    Object versionObj = cache.getIfPresent("render-version");
                    if (versionObj == null || !Objects.equals(version, versionObj)) {
                        cache.put("render-version", version);
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
        Runnable task = () -> {
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
              if (taskIdObj == null || !Objects.equals(taskId, taskIdObj)) {
                  cache.put("taskId", taskId);
                  map.put("sys", "COLLECT");
                  map.put("taskId", taskId);
                  map.put("handleVehicle", handleVehicle);
                  map.put("taskStatus", taskStatus);
                  map.put("collectRoad", collectRoad);
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
