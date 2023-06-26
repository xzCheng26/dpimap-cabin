package com.dpi.map.cabin.server;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dpi.map.cabin.common.Const;
import com.dpi.map.cabin.domain.GuideLineResponse;
import com.dpi.map.cabin.domain.ObstacleResponse;
import com.dpi.map.cabin.domain.PositionResponse;
import com.dpi.map.cabin.servlet.BeanContext;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class TCPServer {

    private Cache<String, Object> cache = BeanContext.getBean(Cache.class);

    private int port;
    private boolean isFinished;
    private ServerSocket serverSocket;
    private ArrayList<SocketThread> socketThreads;

    public TCPServer(int port) {
        this.port = port;
        socketThreads = new ArrayList<>();
    }

    public void start() {
        isFinished = false;
        try {
            //创建服务器套接字，绑定到指定的端口
            serverSocket = new ServerSocket(port);
            //等待客户端连接
            while (!isFinished) {
                Socket socket = serverSocket.accept();//接受连接
                //创建线程处理连接
                SocketThread socketThread = new SocketThread(socket);
                socketThreads.add(socketThread);
                socketThread.start();
            }
        } catch (IOException e) {
            isFinished = true;
        }
    }


    public void stop() {
        isFinished = true;
        for (SocketThread socketThread : socketThreads) {
            socketThread.interrupt();
            socketThread.close();
        }
        try {
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SocketThread extends Thread {

        private Socket socket;
        private InputStream in;
        private OutputStream out;

        SocketThread(Socket socket) {
            this.socket = socket;
            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                if (in == null) {
                    return;
                }
                String logFilePath =  cache.getIfPresent("logFilePath").toString();
                File position = new File("D:\\dpi-515\\logs\\" + logFilePath + "\\position.txt");
                FileWriter positionWriter = null;
                File obs = new File("D:\\dpi-515\\logs\\" + logFilePath + "\\obs.txt");
                FileWriter obsfileWriter = null;
                File line = new File("D:\\dpi-515\\logs\\" + logFilePath + "\\line.txt");
                FileWriter lineWriter = null;
                File speed = new File("D:\\dpi-515\\logs\\" + logFilePath + "\\speed.txt");
                FileWriter speedWriter = null;
                File errorInfo = new File("D:\\dpi-515\\logs\\error.txt");
                FileWriter errorWriter = null;
                try {
                    lineWriter = new FileWriter(line, true);
                    positionWriter = new FileWriter(position, true);
                    obsfileWriter = new FileWriter(obs, true);
                    speedWriter = new FileWriter(speed, true);
                    errorWriter = new FileWriter(errorInfo, true);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {

//                    int tryTimes = 0;
//                    int count = in.available();
//                    while (count == 0) {
//                        count = in.available();
//                        tryTimes++;
//                        if (tryTimes > 100 && count==0) {
//                            break;
//                        }
//                    }

                    int available = in.available();
                    if (available > 0) {
                        byte[] buffer = new byte[available];
                        int size = in.read(buffer);
//                        System.out.println("size:   " + size);
                        if (size > 0) {
                            String data = new String(buffer,0,size);
//                            System.out.println("TCPClient say :" + data);
                            //返回结果给TcpClient
                            String response = "success";

//                            String utmCode = String.format("EPSG:%d%s", 32600 + Const.ZONE_NUMBER, Const.IS_NORTHERN_HEMISPHERE ? "" : "+south");
//                            CoordinateReferenceSystem utmCrs = CRS.decode(utmCode);
//                            // 获取WGS84投影的CoordinateReferenceSystem
//                            CoordinateReferenceSystem wgs84Crs = CRS.decode("EPSG:4326");


                            if (port == 30000) {
                                // 实时位置
                                JSONObject potionData = null;
                                try {
                                    potionData = JSONObject.parseObject(data);
                                } catch (Exception e) {
                                    out.write(response.getBytes());
                                    out.flush();
                                    log.error("实时位置异常，data:{},errorMsg:{}, e:{}", data, e.getMessage(), e);
                                    continue;
                                }
                                JSONObject local = potionData.getJSONObject("gpsPosition");
                                PositionResponse positionResponse = new PositionResponse();
                                if (local != null &&
                                        local.getDouble("latitude") != null
                                        && local.getDouble("longitude") != null
                                        && local.getDouble("yaw") != null) {
                                    positionResponse.setCurrentTime(DateUtil.formatDateTime(new Date()));
                                    positionResponse.setYaw(local.getDouble("yaw"));
                                    positionResponse.setLongitude(local.getDouble("longitude"));
                                    positionResponse.setLatitude(local.getDouble("latitude"));

                                    JSONObject velocity = potionData.getJSONObject("velocity");
                                    if (velocity.getDouble("velocity_x") != null
                                            && velocity.getDouble("velocity_y") != null) {
                                        positionResponse.setSpeedX(velocity.getDouble("velocity_x"));
                                        positionResponse.setSpeedY(velocity.getDouble("velocity_y"));

                                        double speedVeloc = Math.sqrt(Math.pow(velocity.getDouble("velocity_x"), 2)+Math.pow(velocity.getDouble("velocity_y"), 2));
                                        double speedDouble = (speedVeloc * 36) / 10;
                                        Double doubleValueObject = new Double(speedDouble);
                                        int speedInt = doubleValueObject.intValue();
                                        cache.put("speed", speedInt);
                                    }
                                    String potionStr = JSONUtil.toJsonStr(positionResponse);
                                    PositionWebSocket.sendInfoToClient(potionStr);
                                    PrintWriter pw = new PrintWriter(positionWriter);
                                    pw.println(potionStr);
                                    pw.flush();
                                    positionWriter.flush();
                                    pw.close();
                                } else {
                                    PrintWriter pw = new PrintWriter(positionWriter);
                                    pw.println("{}");
                                    pw.flush();
                                    positionWriter.flush();
                                    pw.close();
                                }
                                out.write(response.getBytes());
                                out.flush();

                            } else if (port == 30001) {
                                // 障碍物
                                JSONObject obsData = null;
                                try {
                                    obsData = JSONObject.parseObject(data);
                                } catch (Exception e) {
                                    out.write(response.getBytes());
                                    out.flush();
                                    log.error("障碍物异常，data:{},errorMsg:{}, e:{}", data, e.getMessage(), e);
                                    continue;
                                }
                                JSONArray jsonArray = obsData.getJSONArray("objectArray");
                                List<ObstacleResponse> obstacleResponseList = Lists.newArrayList();
                                if (jsonArray.size() != 0) {
                                    for (int i = 0; i < jsonArray.size() ; i++) {
                                        JSONObject local = jsonArray.getJSONObject(i);
//                                        // 创建坐标转换
//                                        MathTransform transform = CRS.findMathTransform(utmCrs, wgs84Crs);
//                                        // 创建UTM坐标
//                                        DirectPosition2D utmCoord = new DirectPosition2D(utmCrs, local.getDouble("objectPoint_x"), local.getDouble("objectPoint_y"));
//                                        // 将UTM坐标转换为WGS84坐标
//                                        DirectPosition2D wgs84Coord = new DirectPosition2D();
//                                        transform.transform(utmCoord, wgs84Coord);
                                        if (local != null
                                                && local.getDouble("objectPoint_y") != null
                                                && local.getDouble("objectPoint_x") != null
                                                && local.getDouble("objectPoint_heading") != null
                                                && local.getDouble("objectPoint_type") != null) {
                                            ObstacleResponse obstacleResponse = new ObstacleResponse();
                                            obstacleResponse.setCurrentTime(DateUtil.formatDateTime(new Date()));
                                            obstacleResponse.setLongitude(local.getDouble("objectPoint_x"));
                                            obstacleResponse.setLatitude(local.getDouble("objectPoint_y"));
                                            obstacleResponse.setHeading(local.getDouble("objectPoint_heading"));
                                            obstacleResponse.setType(local.getInteger("objectPoint_type"));

                                            obstacleResponseList.add(obstacleResponse);
                                        }
                                    }
                                    String obsStr = JSONUtil.toJsonStr(obstacleResponseList);
                                    ObstacleWebSocket.sendInfoToClient(obsStr);

                                }
                                String obsStr = JSONUtil.toJsonStr(obstacleResponseList);
                                PrintWriter pw = new PrintWriter(obsfileWriter);
                                pw.println(obsStr);
                                pw.flush();
                                obsfileWriter.flush();
                                pw.close();

                                out.write(response.getBytes());
                                out.flush();

                            } else if (port == 30002) {
                                // 引导线
//                                System.out.println(data);
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = JSONObject.parseObject(data);
                                } catch (Exception e) {
                                    out.write(response.getBytes());
                                    out.flush();
                                    continue;
                                }
                                JSONArray jsonArray = jsonObject.getJSONArray("navigation_guide_line");
                                List<GuideLineResponse> guideLineResponseArrayList = Lists.newArrayList();
                                if (jsonArray.size() != 0) {
                                    for (int i = 0; i < jsonArray.size() ; i++) {
                                        JSONObject local = jsonArray.getJSONObject(i);
                                        if (local != null
                                                && local.getDouble("trajectPoint_y") != null
                                                && local.getDouble("trajectPoint_x") != null) {
                                            GuideLineResponse guideLineResponse = new GuideLineResponse();
                                            guideLineResponse.setCurrentTime(DateUtil.formatDateTime(new Date()));
                                            guideLineResponse.setLongitude(local.getDouble("trajectPoint_x"));
                                            guideLineResponse.setLatitude(local.getDouble("trajectPoint_y"));

                                            guideLineResponseArrayList.add(guideLineResponse);
                                        }

//                                        // 创建坐标转换
//                                        MathTransform transform = CRS.findMathTransform(utmCrs, wgs84Crs);
//                                        // 创建UTM坐标
//                                        DirectPosition2D utmCoord = new DirectPosition2D(utmCrs, local.getDouble("trajectPoint_x"), local.getDouble("trajectPoint_y"));
//                                        // 将UTM坐标转换为WGS84坐标
//                                        DirectPosition2D wgs84Coord = new DirectPosition2D();
//                                        transform.transform(utmCoord, wgs84Coord);


                                    }
                                    String lineStr = JSONUtil.toJsonStr(guideLineResponseArrayList);
                                    GuidelineWebSocket.sendInfoToClient(lineStr);
                                }
                                String lineStr = JSONUtil.toJsonStr(guideLineResponseArrayList);
                                PrintWriter pw = new PrintWriter(lineWriter);
                                pw.println(lineStr);
                                pw.flush();
                                lineWriter.flush();
                                pw.close();

                                out.write(response.getBytes());
                                out.flush();
                            }
                            else if (port == 30003) {
                                int speedInt = (int) cache.getIfPresent("speed");
                                ImmutableMap map = ImmutableMap.of("speed", speedInt, "currentTime", DateUtil.formatDateTime(new Date()));
                                String speedStr = JSONUtil.toJsonStr(map);
                                SpeedWebSocket.sendInfoToClient(speedStr);
                                PrintWriter pw = new PrintWriter(speedWriter);
                                pw.println(speedStr);
                                pw.flush();
                                speedWriter.flush();
                                pw.close();

                                out.write(response.getBytes());
                                out.flush();

                                Thread.sleep(500);
                            }
                        }
                    }

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    PrintWriter pw = new PrintWriter(errorWriter);
                    pw.println(e.getMessage());
                    pw.flush();
                    try {
                        errorWriter.flush();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    pw.close();

                    try {
                        out.write("success".getBytes());
                        out.flush();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                finally {
                    try {
                        obsfileWriter.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        positionWriter.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        lineWriter.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        errorWriter.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        speedWriter.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        void close() {

            try {
                if (in != null) {
                    in.close();
                }

                if (out != null) {
                    out.close();
                }

                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


