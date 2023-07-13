package com.dpi.map.cabin.server;

import cn.hutool.core.date.DateUtil;
import com.dpi.map.cabin.domain.PositionResponse;
import com.dpi.map.cabin.domain.proto.DpiMeOutput;
import com.dpi.map.cabin.servlet.BeanContext;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Date;

@Slf4j
public class ConsultationReciveSocket implements Runnable{
    DatagramSocket socket = null;

    private int port;

    private String sendName;

    public ConsultationReciveSocket(int port, String sendName) throws SocketException {
        this.port = port;
        this.sendName = sendName;
        socket = new DatagramSocket(this.port);
    }
    @Override
    public void run() {
        while (true) {
            String msg = null;
            try {
                byte[] bytes = new byte[1024];
                DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
                socket.receive(dp);
                byte[] lengthBytes = new byte[2];
                lengthBytes[0]= dp.getData()[14];
                lengthBytes[1]= dp.getData()[15];
                int length = byteArrayToInt(lengthBytes);
                byte[] point = new byte[length];
                System.arraycopy(bytes,17,point,0, length);

                DpiMeOutput.PositionMsg positionMsg = DpiMeOutput.PositionMsg.parseFrom(point);

                PositionResponse positionResponse = new PositionResponse();
                positionResponse.setLongitude(positionMsg.getVehiclePos().getPoint6D().getX());
                positionResponse.setLatitude(positionMsg.getVehiclePos().getPoint6D().getY());
                double degrees = Math.toDegrees(positionMsg.getVehiclePos().getPoint6D().getYaw());
                positionResponse.setYaw((float) degrees);
                positionResponse.setCurrentTime(DateUtil.formatDateTime(new Date()));
                positionResponse.setPitch(positionMsg.getVehiclePos().getPoint6D().getPitch());
                positionResponse.setRoll(positionMsg.getVehiclePos().getPoint6D().getRoll());
                positionResponse.setAzimuth((float) degrees);

                Cache<String, Object> cache = BeanContext.getBean(Cache.class);
                cache.put("longitude", positionMsg.getVehiclePos().getPoint6D().getX());
                cache.put("latitude", positionMsg.getVehiclePos().getPoint6D().getY());
                cache.put("yaw", (float) degrees);
                cache.put("pitch", positionMsg.getVehiclePos().getPoint6D().getPitch());
                cache.put("roll", positionMsg.getVehiclePos().getPoint6D().getRoll());
                cache.put("azimuth", (float) degrees);
                log.info("定位数据已存储到缓存......");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static int byteArrayToInt(byte[] bytes){
        int res = 0;
        for (int i = 0; i < bytes.length; i++) {
            res += (bytes[i] & 0xff) << i*8;
        }
        return res;
    }
}
