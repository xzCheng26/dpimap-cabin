//package com.dpi.map.cabin.server;
//
//import cn.hutool.json.JSONUtil;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.Lists;
//import org.apache.commons.collections4.MultiValuedMap;
//import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
//import org.springframework.core.io.ClassPathResource;
//
//import javax.annotation.concurrent.Immutable;
//import java.io.*;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//import java.net.SocketAddress;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.AbstractMap;
//import java.util.LinkedList;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public abstract class TCPClient {
//
//    private String ip;
//    private int port;
//
//    private Socket mSocket;
//    private SocketAddress mSocketAddress;
//    private OutputStream mOutputStream;
//    private InputStream mInputStream;
//    private ReadThread mReadThread;
//    private boolean _isConnected = false;
//
//
//    public TCPClient(String ip, int port) {
//        this.ip = ip;
//        this.port = port;
//    }
//
//
//    public void connect() {
//
//        try {
//            this.mSocket = new Socket();
//            this.mSocket.setKeepAlive(true);
//            this.mSocketAddress = new InetSocketAddress(ip, port);
//            this.mSocket.connect( mSocketAddress, 3000);// 设置连接超时时间为3秒
//
//            this.mOutputStream = mSocket.getOutputStream();
//            this.mInputStream = mSocket.getInputStream();
//
//            this.mReadThread = new ReadThread();
//            this.mReadThread.start();
//            this._isConnected = true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void close() {
//
//        if (this.mReadThread != null) {
//            this.mReadThread.interrupt();
//        }
//        if (this.mSocket != null) {
//            try {
//                this.mSocket.close();
//                this.mSocket = null;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        this._isConnected = false;
//    }
//
//    public boolean isConnected() {
//        return this._isConnected;
//    }
//
//
//    public void send(byte[] bOutArray) {
//        try {
//            this.mOutputStream.write(bOutArray);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    protected abstract void onDataReceive(byte[] bytes, int size);
//
//    private class ReadThread extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            while (!isInterrupted()) {
//                try {
//                    if (TCPClient.this.mInputStream == null) {
//                        return;
//                    }
//                    int available = TCPClient.this.mInputStream.available();
//                    if (available > 0) {
//                        byte[] buffer = new byte[available];
//                        int size = TCPClient.this.mInputStream.read(buffer);
//                        if (size > 0) {
//                            onDataReceive(buffer, size);
//                        }
//                    } else {
//                        Thread.sleep(50);
//                    }
//
//
//                } catch (Throwable e) {
//                    System.out.println(e.getMessage());
//                    return;
//                }
//            }
//        }
//    }
//
//  public static void main(String[] args) throws IOException {
//
////      Thread thread1 = new Thread(() -> {
////          //实时位置
////          buildPosition();
////      });
////      thread1.start();
////
////      Thread thread2 = new Thread(() -> {
////          // 障碍物
////          buildObs();
////      });
////      thread2.start();
//
//      Thread thread3 = new Thread(() -> {
//          // 引导线
//          buildGuideLine();
//      });
//      thread3.start();
////
////      TCPClient tcpClient3 = new TCPClient("127.0.0.1", 30002) {
////          @Override
////          protected void onDataReceive(byte[] bytes, int size) {
////              String content = "TCPServer say :" + new String(bytes, 0, size);
////              System.out.println(content);
////          }
////      };
////      tcpClient3.connect();//连接TCPServer
//
//
////      if (tcpClient3.isConnected()) {
////          String fileName = "doc/fix_potion_back.txt";
////          ClassPathResource classPathResource = new ClassPathResource(fileName);
////          //获取文件
////          File file = classPathResource.getFile();
////          LinkedList<Integer> speedList = Lists.newLinkedList();
////          try (BufferedReader br = Files.newBufferedReader(Paths.get(file.getPath()), StandardCharsets.UTF_8)) {
////              for (String line = null; (line = br.readLine()) != null; ) {
////                  JSONObject jsonObject = JSONObject.parseObject(line);
////                  if (jsonObject.getDouble("speedX") != null
////                          && jsonObject.getDouble("speedX") != 0
////                          && jsonObject.getDouble("speedY") != null
////                          && jsonObject.getDouble("speedY") != 0) {
////                      double speed = Math.sqrt(Math.pow(jsonObject.getDouble("speedX"), 2)+Math.pow(jsonObject.getDouble("speedY"), 2));
////                      ImmutableMap<String, String> map = ImmutableMap.of("speed", String.valueOf((int)speed));
//////                  SpeedWebSocket.sendInfoToClient(JSONUtil.toJsonStr(map));
//////                  System.out.println(JSONUtil.toJsonStr(map));
////                      tcpClient3.send(JSONUtil.toJsonStr(map).getBytes());
////                      Thread.sleep(500);
////                  }
////              }
////
////          } catch (IOException e) {
////              throw new RuntimeException(e);
////          } catch (InterruptedException e) {
////              throw new RuntimeException(e);
////          }
////      }
////      tcpClient3.close();
//
//
//
////      TCPClient tcpClient1 = new TCPClient("127.0.0.1", 30000) {
////          @Override
////          protected void onDataReceive(byte[] bytes, int size) {
////              String content = "TCPServer say :" + new String(bytes, 0, size);
////              System.out.println(content);
////          }
////      };
////      tcpClient1.connect();//连接TCPServer
////
////      TCPClient tcpClient2 = new TCPClient("127.0.0.1", 30001) {
////          @Override
////          protected void onDataReceive(byte[] bytes, int size) {
////              String content = "TCPServer say :" + new String(bytes, 0, size);
////              System.out.println(content);
////          }
////      };
////      tcpClient2.connect();//连接TCPServer
////
////      TCPClient tcpClient4 = new TCPClient("127.0.0.1", 30003) {
////          @Override
////          protected void onDataReceive(byte[] bytes, int size) {
////              String content = "TCPServer say :" + new String(bytes, 0, size);
////              System.out.println(content);
////          }
////      };
////      tcpClient4.connect();//连接TCPServer
////
////
////      MultiValuedMap<String, String> mapLine = new ArrayListValuedHashMap<>();
////      MultiValuedMap<String, String> mapObs = new ArrayListValuedHashMap<>();
////      MultiValuedMap<String, String> mapPosition = new ArrayListValuedHashMap<>();
////      MultiValuedMap<String, String> mapSpeed = new ArrayListValuedHashMap<>();
//////        new Thread(
//////                () -> {
////      String filePath = "D:\\dpi-515\\logs\\1684134308299-小圈可用\\line.txt";
////
////      try (BufferedReader br =
////                   Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
////          for (String line; (line = br.readLine()) != null; ) {
////              if (!"{}".equals(line)) {
////
////                  JSONArray jsonArray = JSONArray.parseArray(line);
////                  if (jsonArray.size() > 0) {
////                      JSONObject object = (JSONObject) jsonArray.get(0);
////                      String currentTime = (String) object.get("currentTime");
////                      mapLine.put(currentTime, line);
////                      if (jsonArray.size() != 0) {
////                          for (int i = 0; i < jsonArray.size() ; i++) {
////                              JSONObject local = jsonArray.getJSONObject(i);
////                              if (local.getString("longitude") != null && !local.getString("longitude").startsWith("116")
////                                      && local.getString("latitude") != null && !local.getString("latitude").startsWith("39")) {
////                                  jsonArray.remove(i);
////                              }
////                          }
//////                                    System.out.println("引导线：  " + line);
//////                                    GuidelineWebSocket.sendInfoToClient(line);
//////                                    Thread.sleep(100);
////                      }
////                  }
////
////              }
////          }
////      } catch (IOException e) {
////          throw new RuntimeException(e);
////      }
//////                })
//////                .start();
////
//////        new Thread(()-> {
////      String filePath2 = "D:\\dpi-515\\logs\\1684134308299-小圈可用\\potion.txt";
////      try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath2), StandardCharsets.UTF_8)) {
////          for (String line; (line = br.readLine()) != null;) {
////              if (!"{}".equals(line)) {
////                  JSONObject obj = JSONObject.parseObject(line);
////                  if (obj.getString("longitude") != null && obj.getString("longitude").startsWith("116") &&
////                          obj.getString("latitude") != null && obj.getString("latitude").startsWith("39")) {
////                      mapPosition.put(obj.getString("currentTime"), line);
//////                            PositionWebSocket.sendInfoToClient(line);
//////                            System.out.println("实时位置：  " +line);
//////                            Thread.sleep(50);
////                  }
////
////              }
////          }
////      } catch (IOException e) {
////          throw new RuntimeException(e);
////      }
//////        }).start();
////
//////        new Thread(()-> {
////      String filePath3 = "D:\\dpi-515\\logs\\1684134308299-小圈可用\\obs.txt";
////      try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath3), StandardCharsets.UTF_8)) {
////          for (String line; (line = br.readLine()) != null;) {
////
////              if (!"[]".equals(line)) {
////                  JSONArray jsonArray = JSONArray.parseArray(line);
////                  if (jsonArray.size() > 0) {
////                      JSONObject object = (JSONObject) jsonArray.get(0);
////                      String currentTime = (String) object.get("currentTime");
////                      mapObs.put(currentTime, line);
////                      if (jsonArray.size() != 0) {
////                          boolean flag = false;
////                          for (int i = 0; i < jsonArray.size() ; i++) {
////                              JSONObject local = jsonArray.getJSONObject(i);
////                              if (local.getString("longitude") != null && !local.getString("longitude").startsWith("116")
////                                      && local.getString("latitude") != null && !local.getString("latitude").startsWith("39")) {
////                                  jsonArray.remove(i);
////                              }
////                          }
//////                            System.out.println("障碍物： " + JSONUtil.toJsonStr(jsonArray));
//////                            ObstacleWebSocket.sendInfoToClient(JSONUtil.toJsonStr(jsonArray));
//////                            Thread.sleep(100);
////                      }
////                  }
////
////              }
////
////          }
////      } catch (IOException e) {
////          throw new RuntimeException(e);
////      }
//////        }).start();
////
//////        new Thread(()-> {
////      String filePath4 = "D:\\dpi-515\\logs\\1684134308299-小圈可用\\speed.txt";
////      try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath4), StandardCharsets.UTF_8)) {
////          for (String line; (line = br.readLine()) != null;) {
////              if (!"{}".equals(line)) {
////                  JSONObject object = JSONObject.parseObject(line);
////                  mapSpeed.put(object.getString("currentTime"), line);
//////                        System.out.println("车速：  " +line);
//////                        SpeedWebSocket.sendInfoToClient(line);
//////                        Thread.sleep(500);
////              }
////          }
////      } catch (IOException e) {
////          throw new RuntimeException(e);
////      }
//////        }).start();
////
////
////
////      Map<String, String> lineMap = mapLine.asMap().entrySet().stream().map(
////              entry -> {
////                  StringBuilder valuesBuilder = new StringBuilder();
////                  entry.getValue().forEach(value -> valuesBuilder.append(value).append("/"));
////                  String values = valuesBuilder.deleteCharAt(valuesBuilder.length() - 1).toString();
////                  return new AbstractMap.SimpleEntry<String, String>(entry.getKey(), values);
////              }
////      ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
////
////      Map<String, String> obsMap = mapObs.asMap().entrySet().stream().map(
////              entry -> {
////                  StringBuilder valuesBuilder = new StringBuilder();
////                  entry.getValue().forEach(value -> valuesBuilder.append(value).append("/"));
////                  String values = valuesBuilder.deleteCharAt(valuesBuilder.length() - 1).toString();
////                  return new AbstractMap.SimpleEntry<String, String>(entry.getKey(), values);
////              }
////      ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
////
////      Map<String, String> positionMap = mapPosition.asMap().entrySet().stream().map(
////              entry -> {
////                  StringBuilder valuesBuilder = new StringBuilder();
////                  entry.getValue().forEach(value -> valuesBuilder.append(value).append("/"));
////                  String values = valuesBuilder.deleteCharAt(valuesBuilder.length() - 1).toString();
////                  return new AbstractMap.SimpleEntry<String, String>(entry.getKey(), values);
////              }
////      ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
////
////      Map<String, String> speedMap = mapSpeed.asMap().entrySet().stream().map(
////              entry -> {
////                  StringBuilder valuesBuilder = new StringBuilder();
////                  entry.getValue().forEach(value -> valuesBuilder.append(value).append("/"));
////                  String values = valuesBuilder.deleteCharAt(valuesBuilder.length() - 1).toString();
////                  return new AbstractMap.SimpleEntry<String, String>(entry.getKey(), values);
////              }
////      ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
////
////
////      positionMap.forEach((k, v) -> {
////          String[] positions = v.split("/");
////
////
////          String line = lineMap.get(k);
////          String[] lines = line.split("/");
////
////
////
////          String obs = obsMap.get(k);
////          String[] obss = new String[0];
////          if (obs != null) {
////              obss = obs.split("/");
////          }
////
////
////          String speed = speedMap.get(k);
////          String[] speeds = speed.split("/");
////          if (tcpClient1.isConnected()) {
////              new Thread(
////                      () -> {
////                          try {
////                              for (String li : lines) {
////                                  System.out.println("引导线：" + li);
////                                  tcpClient1.send(li.getBytes());
//////                              GuidelineWebSocket.sendInfoToClient(li);
////                                  Thread.sleep(100);
////                              }
////
////                          } catch (Exception e) {
////                              throw new RuntimeException(e);
////                          }
////
////                      }).start();
////          }
////
////          if (tcpClient2.isConnected()) {
////              String[] finalPositions = positions;
////              new Thread(
////                      () -> {
////                          try {
////                              for (String po : finalPositions) {
////                                  System.out.println("实时位置：" + po);
////                                  tcpClient2.send(po.getBytes());
//////                              PositionWebSocket.sendInfoToClient(po);
////                                  Thread.sleep(100);
////                              }
////                          } catch (Exception e) {
////                              throw new RuntimeException(e);
////                          }
////
////                      }).start();
////
////
////          }
////          if (tcpClient3.isConnected()) {
////              String[] finalObss = obss;
////              new Thread(
////                      () -> {
////                          try {
////                              for (String ob : finalObss) {
////                                  System.out.println("障碍物：" + ob);
////                                  tcpClient3.send(ob.getBytes());
//////                              ObstacleWebSocket.sendInfoToClient(ob);
////                                  Thread.sleep(100);
////                              }
////                          } catch (Exception e) {
////                              throw new RuntimeException(e);
////                          }
////
////                      }).start();
////          }
////
////          if (tcpClient4.isConnected()) {
////              String[] finalSpeeds = speeds;
////              new Thread(
////                      () -> {
////                          try {
////                              for (String sp : finalSpeeds) {
////                                  System.out.println("速度：" + sp);
////                                  tcpClient4.send(sp.getBytes());
////
//////                              SpeedWebSocket.sendInfoToClient(sp);
////                                  Thread.sleep(500);
////                              }
////                          } catch (Exception e) {
////                              throw new RuntimeException(e);
////                          }
////
////                      }).start();
////          }
////
////      });
//
//
//  }
//
//
//
//
//
//
////  }
//
//    private static void buildGuideLine() {
//        TCPClient tcpClient3 = new TCPClient("127.0.0.1", 30002) {
//            @Override
//            protected void onDataReceive(byte[] bytes, int size) {
//                String content = "TCPServer say :" + new String(bytes, 0, size);
//                System.out.println(content);
//            }
//        };
//
//        tcpClient3.connect();//连接TCPServer
//        if (tcpClient3.isConnected()) {
//            String cc = "{\n" +
//                    "\t\"name\":\t\"Trajectory\",\n" +
//                    "\t\"navigation_guide_line\":\t[{\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456239.01884085586,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397754.75104788\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456238.50126866554,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397754.4678136064\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456237.98192125082,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397754.18472878\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456237.4592633649,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397753.9016447244\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456236.93199140823,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397753.6181437057\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456236.39900561562,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397753.33359079\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456235.85938513436,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397753.0471802829\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456235.31236293,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397752.7579825073\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456234.75729747,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397752.4649965819\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456234.19363805238,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397752.1672150372\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456233.62088045891,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397751.86370648\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456233.03850936674,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397751.5537229236\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456232.44592367264,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397751.23683898\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456231.84234154032,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397750.9131288333\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456231.2269202022,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397750.582942944\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456230.59916748776,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397750.2461410258\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456229.958710575,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397749.9025230482\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456229.30524442723,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397749.5519253565\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456228.63853179425,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397749.19422066\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456227.95840321138,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397748.8293180363\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456227.26475700014,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397748.4571629334\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456226.55755926686,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397748.07773717\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456225.83684390486,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397747.6910589254\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456225.102712593,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397747.2971827555\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456224.35533479578,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397746.89619958\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456223.59494776366,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397746.48823669\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456222.82185653271,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397746.073457744\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456222.03643392574,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397745.6520627663\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456221.23912055138,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397745.22428815\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456220.4304248055,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397744.7904066574\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456219.61092286662,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397744.3507274222\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456218.78125869349,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397743.9055959573\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456217.942144024,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397743.4553941563\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456217.09435839136,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397743.000540264\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456216.23874915484,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397742.54148882\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456215.37623151578,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397742.078730626\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456214.50778845546,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397741.6127928644\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456213.63447054755,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397741.1442394443\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456212.75739574549,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397740.6736714039\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456211.87774950149,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397740.2017266816\n" +
//                    "\t\t}, {\n" +
//                    "\t\t\t\"trajectPoint_x\":\t456210.9967856335,\n" +
//                    "\t\t\t\"trajectPoint_y\":\t4397739.7290785043\n" +
//                    "\t\t}]\n" +
//                    "}\n";
//            //发送数据
//            for (int i=0;i<5;i++) {
//                tcpClient3.send(cc.getBytes());
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//
//            }
//
////            String cc = "我是引导线";
////            for (int i=0;i<20;i++) {
////                tcpClient3.send(cc.getBytes());
////                try {
////                    Thread.sleep(500);
////                } catch (InterruptedException e) {
////                    throw new RuntimeException(e);
////                }
////            }
//        }
//
//
//        //关闭连接
////        tcpClient3.close();
//    }
//
////    private static void buildObs() {
////        TCPClient tcpClient2 = new TCPClient("127.0.0.1", 30001) {
////            @Override
////            protected void onDataReceive(byte[] bytes, int size) {
////                String content = "TCPServer say :" + new String(bytes, 0, size);
////                System.out.println(content);
////            }
////        };
////        tcpClient2.connect();//连接TCPServer
////        if (tcpClient2.isConnected()) {
////            //发送数据
////            String cc = "我是障碍物";
////            for (int i=0;i<20;i++) {
////                tcpClient2.send(cc.getBytes());
////                try {
////                    Thread.sleep(100);
////                } catch (InterruptedException e) {
////                    throw new RuntimeException(e);
////                }
////            }
////        }
////
////        //关闭连接
////        tcpClient2.close();
////    }
////
////    private static void buildPosition() {
////        TCPClient tcpClient = new TCPClient("127.0.0.1", 30000) {
////            @Override
////            protected void onDataReceive(byte[] bytes, int size) {
////                String content = "TCPServer say :" + new String(bytes, 0, size);
////                System.out.println(content);
////            }
////        };
////        tcpClient.connect();//连接TCPServer
////        if (tcpClient.isConnected()) {
////            //发送数据
////            String outContent = "TCPClient say hello ooooo1";
////            String bb = "我是实时位置";
////            for (int i=0;i<50;i++) {
////                tcpClient.send(bb.getBytes());
////                try {
////                    Thread.sleep(100);
////                } catch (InterruptedException e) {
////                    throw new RuntimeException(e);
////                }
////            }
////
////        }
////
////        //关闭连接
////        tcpClient.close();
////    }
//}
//
//
