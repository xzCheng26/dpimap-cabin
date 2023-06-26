//package com.dpi.map.cabin.servlet;
//
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.json.JSONUtil;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.dpi.map.cabin.domain.GuideLineResponse;
//import com.dpi.map.cabin.server.GuidelineWebSocket;
//import com.dpi.map.cabin.server.ObstacleWebSocket;
//import com.dpi.map.cabin.server.PositionWebSocket;
//import com.dpi.map.cabin.server.SpeedWebSocket;
//import org.apache.commons.collections4.MultiValuedMap;
//import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
//import org.apache.commons.lang3.time.DateUtils;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.stream.Collectors;
//@Component
//public class PostConstructTest {
//
//    @PostConstruct()
//    public void bb () {
//        MultiValuedMap<String, String> mapLine = new ArrayListValuedHashMap<>();
//        MultiValuedMap<String, String> mapObs = new ArrayListValuedHashMap<>();
//        MultiValuedMap<String, String> mapPosition = new ArrayListValuedHashMap<>();
//        MultiValuedMap<String, String> mapSpeed = new ArrayListValuedHashMap<>();
////        new Thread(
////                () -> {
//                    String filePath = "D:\\dpi-515\\logs\\1684134308299-小圈可用\\line.txt";
//
//                    try (BufferedReader br =
//                                 Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
//                        for (String line; (line = br.readLine()) != null; ) {
//                            if (!"{}".equals(line)) {
//
//                                JSONArray jsonArray = JSONArray.parseArray(line);
//                                if (jsonArray.size() > 0) {
//                                    JSONObject object = (JSONObject) jsonArray.get(0);
//                                    String currentTime = (String) object.get("currentTime");
//                                    mapLine.put(currentTime, line);
//                                    if (jsonArray.size() != 0) {
//                                        for (int i = 0; i < jsonArray.size() ; i++) {
//                                            JSONObject local = jsonArray.getJSONObject(i);
//                                            if (local.getString("longitude") != null && !local.getString("longitude").startsWith("116")
//                                                    && local.getString("latitude") != null && !local.getString("latitude").startsWith("39")) {
//                                                jsonArray.remove(i);
//                                            }
//                                        }
////                                    System.out.println("引导线：  " + line);
////                                    GuidelineWebSocket.sendInfoToClient(line);
////                                    Thread.sleep(100);
//                                    }
//                                }
//
//                            }
//                        }
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
////                })
////                .start();
//
////        new Thread(()-> {
//            String filePath2 = "D:\\dpi-515\\logs\\1684134308299-小圈可用\\potion.txt";
//            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath2), StandardCharsets.UTF_8)) {
//                for (String line; (line = br.readLine()) != null;) {
//                    if (!"{}".equals(line)) {
//                        JSONObject obj = JSONObject.parseObject(line);
//                        if (obj.getString("longitude") != null && obj.getString("longitude").startsWith("116") &&
//                                obj.getString("latitude") != null && obj.getString("latitude").startsWith("39")) {
//                            mapPosition.put(obj.getString("currentTime"), line);
////                            PositionWebSocket.sendInfoToClient(line);
////                            System.out.println("实时位置：  " +line);
////                            Thread.sleep(50);
//                        }
//
//                    }
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
////        }).start();
//
////        new Thread(()-> {
//            String filePath3 = "D:\\dpi-515\\logs\\1684134308299-小圈可用\\obs.txt";
//            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath3), StandardCharsets.UTF_8)) {
//                for (String line; (line = br.readLine()) != null;) {
//
//                    if (!"[]".equals(line)) {
//                        JSONArray jsonArray = JSONArray.parseArray(line);
//                        if (jsonArray.size() > 0) {
//                            JSONObject object = (JSONObject) jsonArray.get(0);
//                            String currentTime = (String) object.get("currentTime");
//                            mapObs.put(currentTime, line);
//                            if (jsonArray.size() != 0) {
//                                boolean flag = false;
//                                for (int i = 0; i < jsonArray.size() ; i++) {
//                                    JSONObject local = jsonArray.getJSONObject(i);
//                                    if (local.getString("longitude") != null && !local.getString("longitude").startsWith("116")
//                                            && local.getString("latitude") != null && !local.getString("latitude").startsWith("39")) {
//                                        jsonArray.remove(i);
//                                    }
//                                }
////                            System.out.println("障碍物： " + JSONUtil.toJsonStr(jsonArray));
////                            ObstacleWebSocket.sendInfoToClient(JSONUtil.toJsonStr(jsonArray));
////                            Thread.sleep(100);
//                            }
//                        }
//
//                    }
//
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
////        }).start();
//
////        new Thread(()-> {
//            String filePath4 = "D:\\dpi-515\\logs\\1684134308299-小圈可用\\speed.txt";
//            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath4), StandardCharsets.UTF_8)) {
//                for (String line; (line = br.readLine()) != null;) {
//                    if (!"{}".equals(line)) {
//                        JSONObject object = JSONObject.parseObject(line);
//                        mapSpeed.put(object.getString("currentTime"), line);
////                        System.out.println("车速：  " +line);
////                        SpeedWebSocket.sendInfoToClient(line);
////                        Thread.sleep(500);
//                    }
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
////        }).start();
//
//        String format = "yyyy-MM-dd HH:mm:ss";
//        SimpleDateFormat sdf = new SimpleDateFormat(format);
//
//        Map<Date,String> resultLineMap = new LinkedHashMap<>();
//        Map<Date, String> lineMap = mapLine.asMap().entrySet().stream().map(
//                entry -> {
//                    StringBuilder valuesBuilder = new StringBuilder();
//                    entry.getValue().forEach(value -> valuesBuilder.append(value).append("/"));
//                    String values = valuesBuilder.deleteCharAt(valuesBuilder.length() - 1).toString();
//                    try {
//                        return new AbstractMap.SimpleEntry<Date, String>(sdf.parse(entry.getKey()), values);
//                    } catch (ParseException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//        lineMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> resultLineMap.put(x.getKey(), x.getValue()));
//
//        Map<Date,String> resultObsMap = new LinkedHashMap<>();
//        Map<Date, String> obsMap = mapObs.asMap().entrySet().stream().map(
//                entry -> {
//                    StringBuilder valuesBuilder = new StringBuilder();
//                    entry.getValue().forEach(value -> valuesBuilder.append(value).append("/"));
//                    String values = valuesBuilder.deleteCharAt(valuesBuilder.length() - 1).toString();
//                    try {
//                        return new AbstractMap.SimpleEntry<Date, String>(sdf.parse(entry.getKey()), values);
//                    } catch (ParseException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//        obsMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> resultObsMap.put(x.getKey(), x.getValue()));
//
//        Map<Date, String> resultPositionMap = new LinkedHashMap<>();
//        Map<Date, String> positionMap = mapPosition.asMap().entrySet().stream().map(
//                entry -> {
//                    StringBuilder valuesBuilder = new StringBuilder();
//                    entry.getValue().forEach(value -> valuesBuilder.append(value).append("/"));
//                    String values = valuesBuilder.deleteCharAt(valuesBuilder.length() - 1).toString();
//                    try {
//                        return new AbstractMap.SimpleEntry<Date, String>(sdf.parse(entry.getKey()), values);
//                    } catch (ParseException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//        positionMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> resultPositionMap.put(x.getKey(), x.getValue()));
//
//        Map<Date,String> resultSpeedMap = new LinkedHashMap<>();
//        Map<Date, String> speedMap = mapSpeed.asMap().entrySet().stream().map(
//                entry -> {
//                    StringBuilder valuesBuilder = new StringBuilder();
//                    entry.getValue().forEach(value -> valuesBuilder.append(value).append("/"));
//                    String values = valuesBuilder.deleteCharAt(valuesBuilder.length() - 1).toString();
//                    try {
//                        return new AbstractMap.SimpleEntry<Date, String>(sdf.parse(entry.getKey()), values);
//                    } catch (ParseException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//        speedMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> resultSpeedMap.put(x.getKey(), x.getValue()));
//
//
//
//        try {
//            resultPositionMap.forEach((k, v) -> {
//                String[] positions = v.split("/");
//
//                String line = lineMap.get(k);
//                String[] lines = line.split("/");
//
//                String obs = obsMap.get(k);
//                String[] obss = new String[0];
//                if (obs != null) {
//                    obss = obs.split("/");
//                }
//
//                String speed = speedMap.get(k);
//                String[] speeds = speed.split("/");
//
//                new Thread(
//                        () -> {
//                            try {
//                                for (String li : lines) {
//                                    System.out.println("引导线：" + li);
//                                    GuidelineWebSocket.sendInfoToClient(li);
//                                    Thread.sleep(100);
//                                }
//
//                            } catch (Exception e) {
//                                throw new RuntimeException(e);
//                            }
//
//                        }).start();
//
//                String[] finalPositions = positions;
//            new Thread(
//                    () -> {
//                        try {
//                            for (String po : finalPositions) {
//                                System.out.println("实时位置：" + po);
//                                PositionWebSocket.sendInfoToClient(po);
//                                Thread.sleep(100);
//                            }
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//
//                    }).start();
//
//            String[] finalObss = obss;
//            new Thread(
//                    () -> {
//                        try {
//                            for (String ob : finalObss) {
//                                System.out.println("障碍物：" + ob);
//                                ObstacleWebSocket.sendInfoToClient(ob);
//                                Thread.sleep(100);
//                            }
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//
//                    }).start();
//
//            String[] finalSpeeds = speeds;
//            new Thread(
//                    () -> {
//                        try {
//                            for (String sp : finalSpeeds) {
//                                System.out.println("速度：" + sp);
//                                SpeedWebSocket.sendInfoToClient(sp);
//                                Thread.sleep(500);
//                            }
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//
//                    }).start();
//
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//
//
//
//    }
//
//
//
//}
