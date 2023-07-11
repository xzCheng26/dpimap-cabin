package com.dpi.map.cabin.server;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.dpi.map.cabin.domain.PositionResponse;
import com.dpi.map.cabin.domain.proto.DpiMeOutput;
import com.dpi.map.cabin.server.pointcloud.PointPositionWebSocket;
import org.apache.commons.lang3.time.DateUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Date;

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

                PointPositionWebSocket.sendInfoToClient(JSONUtil.toJsonStr(positionResponse));
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
