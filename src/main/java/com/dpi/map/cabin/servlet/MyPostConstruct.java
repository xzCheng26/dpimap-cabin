//package com.dpi.map.cabin.servlet;
//
//import cn.hutool.json.JSONUtil;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.dpi.map.cabin.common.Const;
//import com.dpi.map.cabin.domain.ObstacleOfflineResponse;
//import com.dpi.map.cabin.server.*;
//import com.github.benmanes.caffeine.cache.Cache;
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.Lists;
//import org.apache.commons.io.IOUtils;
//import org.geotools.geometry.DirectPosition2D;
//import org.geotools.referencing.CRS;
//import org.opengis.referencing.FactoryException;
//import org.opengis.referencing.crs.CoordinateReferenceSystem;
//import org.opengis.referencing.operation.MathTransform;
//import org.opengis.referencing.operation.TransformException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Component;
//import org.yaml.snakeyaml.Yaml;
//
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.*;
//
//@Component
//public class MyPostConstruct {
//
//    @Autowired
//    private Cache<String, Object> cache;
//
//    @javax.annotation.PostConstruct
//    public void initTcpServer() {
////                Long cutTime = System.currentTimeMillis();
////                File file = new File(Const.LOG_PATH + "\\" + cutTime);
////                if (!file.exists()){
////                    file.mkdirs();
////                }
////                cache.put("logFilePath", cutTime);
////
////                TCPServer positionServer = new TCPServer(30000);
////                new Thread(()-> {
////                    positionServer.start();
////                }).start();
////                System.out.println("实时位置服务启动");
////
////                TCPServer obsServer = new TCPServer(30001);
////                new Thread(()-> {
////                    obsServer.start();
////                }).start();
////                System.out.println("障碍物服务启动");
////
////                TCPServer lineServer = new TCPServer(30002);
////                new Thread(()-> {
////                    lineServer.start();
////                }).start();
////                System.out.println("引导线服务启动");
////
////                TCPServer speedServer = new TCPServer(30003);
////                new Thread(() -> {
////                    cache.put("speed", 0);
////                    speedServer.start();
////                    })
////                .start();
////                System.out.println("车速服务启动");
//
//
//
//
//    new Thread(
//            () -> {
//              String filePath = "D:\\dpi-515\\quchong\\line525-2.txt";
//              try (BufferedReader br =
//                  Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
//                for (String line; (line = br.readLine()) != null; ) {
//                  if (!"[]".equals(line)) {
////                    JSONArray jsonArray = JSONArray.parseArray(line);
////                    if (jsonArray.size() != 0) {
////                      for (int i = 0; i < jsonArray.size(); i++) {
////                        JSONObject local = jsonArray.getJSONObject(i);
////                        if (local.getString("longitude") != null
////                            && local.getString("longitude").startsWith("116")
////                            && local.getString("latitude") != null
////                            && local.getString("latitude").startsWith("39")) {
////
////                          GuidelineWebSocket.sendInfoToClient(line);
////                          Thread.sleep(100);
////                        }
////                      }
////                    }
////                      System.out.println("引导线：  " + line);
//                      GuidelineWebSocket.sendInfoToClient(line);
//                      Thread.sleep(300);
//                  }
//                }
//              } catch (Exception e) {
//                throw new RuntimeException(e);
//              }
//            })
//        .start();
//
//    new Thread(
//            () -> {
//              String filePath = "D:\\dpi-515\\quchong\\potion525-2.txt";
//              try (BufferedReader br =
//                  Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
//                for (String line; (line = br.readLine()) != null; ) {
//                  if (!"{}".equals(line)) {
//                    JSONObject obj = JSONObject.parseObject(line);
//                    if (obj.getString("longitude") != null
//                        && obj.getString("longitude").startsWith("116")
//                        && obj.getString("latitude") != null
//                        && obj.getString("latitude").startsWith("39")) {
//                      PositionWebSocket.sendInfoToClient(line);
////                      System.out.println("实时位置：  " + line);
//                      Thread.sleep(65);
//                    }
//                  }
//                }
//              } catch (Exception e) {
//                throw new RuntimeException(e);
//              }
//            })
//        .start();
//
//        new Thread(()-> {
//            String filePath = "D:\\dpi-515\\quchong\\obs525-1.txt";
//            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
//                for (String line; (line = br.readLine()) != null;) {
////
////                    if (!"[]".equals(line)) {
////                        JSONArray jsonArray = JSONArray.parseArray(line);
////                        if (jsonArray.size() != 0) {
//////                            System.out.println("障碍物： " + JSONUtil.toJsonStr(jsonArray));
////                            ObstacleWebSocket.sendInfoToClient(JSONUtil.toJsonStr(jsonArray));
//////                            Thread.sleep(100);
////                            Thread.sleep(10);
////                        }
////                    }
//                    ObstacleWebSocket.sendInfoToClient(line);
//                    Thread.sleep(100);
//
//
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
////
//        new Thread(()-> {
//            String filePath = "D:\\dpi-515\\quchong\\speed525-1.txt";
//            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
//                for (String line; (line = br.readLine()) != null;) {
//                    if (!"{}".equals(line)) {
////                        System.out.println("车速：  " +line);
//                        SpeedWebSocket.sendInfoToClient(line);
//                        Thread.sleep(500);
//                    }
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
//
//
//    }
//
////  @javax.annotation.PostConstruct
////  public void speedInfo() throws IOException {
////      String fileName = "doc/fix_potion_back.txt";
////      ClassPathResource classPathResource = new ClassPathResource(fileName);
////      //获取文件
////      File file = classPathResource.getFile();
////      LinkedList<Integer> speedList = Lists.newLinkedList();
////    try (BufferedReader br = Files.newBufferedReader(Paths.get(file.getPath()), StandardCharsets.UTF_8)) {
////      for (String line = null; (line = br.readLine()) != null; ) {
////          JSONObject jsonObject = JSONObject.parseObject(line);
////          if (jsonObject.getDouble("speedX") != null、
////                  && jsonObject.getDouble("speedX") != 0
////                  && jsonObject.getDouble("speedY") != null
////                  && jsonObject.getDouble("speedY") != 0) {
////              double speed = Math.sqrt(Math.pow(jsonObject.getDouble("speedX"), 2)+Math.pow(jsonObject.getDouble("speedY"), 2));
////              SpeedWebSocket.sendInfoToClient(String.valueOf((int)speed));
////              System.out.println((int)speed);
////              Thread.sleep(500);
////          }
////      }
////
////    } catch (IOException e) {
////      throw new RuntimeException(e);
////    } catch (InterruptedException e) {
////        throw new RuntimeException(e);
////    }
////  }
//
//      @javax.annotation.PostConstruct
//    public void offlineObsInfo() {
//      String utmCode = String.format("EPSG:%d%s", 32600 + Const.ZONE_NUMBER, Const.IS_NORTHERN_HEMISPHERE ? "" : "+south");
//      CoordinateReferenceSystem utmCrs = null;
//      try {
//          utmCrs = CRS.decode(utmCode);
//      } catch (FactoryException e) {
//          throw new RuntimeException(e);
//      }
//      // 获取WGS84投影的CoordinateReferenceSystem
//      CoordinateReferenceSystem wgs84Crs = null;
//      try {
//          wgs84Crs = CRS.decode("EPSG:4326");
//      } catch (FactoryException e) {
//          throw new RuntimeException(e);
//      }
//
//      String fileName = "doc/offline_obs.txt";
//      try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);) {
//          String json = IOUtils.toString(is, "utf-8");
//          Yaml yaml = new Yaml();
//          String jsonstr = JSONObject.toJSONString(yaml.load(json));
//
//          JSONObject jsonObject = JSONObject.parseObject(jsonstr);
//          JSONArray jsonArray = jsonObject.getJSONArray("objs");
//          JSONObject header = jsonObject.getJSONObject("header");
//          List<ObstacleOfflineResponse> obstacleResponseList = Lists.newArrayList();
//          for (int i = 0; i < jsonArray.size() ; i++) {
//              JSONObject local = jsonArray.getJSONObject(i);
//              ObstacleOfflineResponse obstacleResponse = new ObstacleOfflineResponse();
//
//              // 创建坐标转换
//              MathTransform transform = CRS.findMathTransform(utmCrs, wgs84Crs);
//              // 创建UTM坐标
//              DirectPosition2D utmCoord = new DirectPosition2D(utmCrs, local.getDouble("xabs"), local.getDouble("yabs"));
//              // 将UTM坐标转换为WGS84坐标
//              DirectPosition2D wgs84Coord = new DirectPosition2D();
//              transform.transform(utmCoord, wgs84Coord);
//
//              obstacleResponse.setOverlapDis(header.getDouble("overlap_dis"));
//              obstacleResponse.setShowDis(header.getDouble("show_dis"));
//              obstacleResponse.setLongitude(wgs84Coord.y);
//              obstacleResponse.setLatitude(wgs84Coord.x);
//
//              obstacleResponse.setHeading(local.getDouble("heading"));
//              obstacleResponse.setType(local.getInteger("type"));
//              obstacleResponseList.add(obstacleResponse);
//          }
//          cache.put("offlineObs", obstacleResponseList);
//      } catch (TransformException | IOException e) {
//          throw new RuntimeException(e);
//      } catch (FactoryException e) {
//          throw new RuntimeException(e);
//      }
//
//    }
//}
