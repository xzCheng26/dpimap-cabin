//package com.dpi.map.cabin.controller;
//
// import cn.hutool.core.date.DateUtil;
// import cn.hutool.core.io.FileUtil;
// import cn.hutool.json.JSONUtil;
// import com.alibaba.fastjson.JSONArray;
// import com.alibaba.fastjson.JSONObject;
// import com.google.common.collect.*;
// import org.apache.commons.lang3.time.DateFormatUtils;
// import org.apache.commons.lang3.time.DateUtils;
//
// import java.io.*;
// import java.nio.charset.Charset;
// import java.nio.charset.StandardCharsets;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.text.ParseException;
// import java.text.SimpleDateFormat;
// import java.util.*;
//
//
//public class Test {
//  public static void main(String[] args) throws IOException, ParseException {
//
////      String filePath = "E:\\My_DPI_Version\\515\\online_data\\potion-back.txt";
////      File potion = new File("E:\\My_DPI_Version\\515\\online_data\\potion-back.txt");
////
////      try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
////          for (String line = null; (line = br.readLine()) != null;) {
////              FileWriter lineWriter = new FileWriter(potion, true);
////              JSONObject jsonObject = JSONObject.parseObject(line);
////              System.out.println(line);
////              Double longitude = jsonObject.getDouble("longitude");
////              if (longitude != 116.4884713) {
////                  PrintWriter pw = new PrintWriter(lineWriter);
////                  pw.println(line);
////                  pw.flush();
//////                  lineWriter.flush();
////                  pw.close();
////              }
////              lineWriter.close();
////          }
////      }
//      LinkedHashMap<String, String> map = Maps.newLinkedHashMap();
//      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
////      String filePath = "D:\\dpi-515\\quchong\\potion525-1.txt";
////      File potion = new File("D:\\dpi-515\\quchong\\potion525-2.txt");
////      try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
////          for (String line = null; (line = br.readLine()) != null;) {
////              JSONObject jsonObject = JSONObject.parseObject(line);
//////              System.out.println(line);
//////              Date start = jsonObject.getDate("currentTime");
//////              long startTime = start.getTime();
//////              if (startTime > 1684203474000L) {
//////               FileWriter lineWriter = new FileWriter(potion, true);
//////               PrintWriter pw = new PrintWriter(lineWriter);
//////               pw.println(line);
//////               pw.flush();
//////               lineWriter.flush();
//////               pw.close();
//////               lineWriter.close();
//////              }
////              String longitude = jsonObject.getString("longitude");
////              String latitude = jsonObject.getString("latitude");
////              Date date = jsonObject.getDate("currentTime");
////              if (date.getTime() > sdf.parse("2023-05-15 15:06:35").getTime()) {
////               FileWriter lineWriter = new FileWriter(potion, true);
////               PrintWriter pw = new PrintWriter(lineWriter);
////               pw.println(line);
////               pw.flush();
////               lineWriter.flush();
////               pw.close();
////               lineWriter.close();
////              }
//////              String key = longitude + ":" + latitude;
//////              map.put(key, line);
////          }
////      }
//
//      String filePath = "D:\\dpi-515\\quchong\\line525-1.txt";
//   File potion = new File("D:\\dpi-515\\quchong\\line525-2.txt");
//    try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
//      for (String line = null; (line = br.readLine()) != null; ) {
//        if (!"[]".equals(line)) {
//          JSONArray jsonArray = JSONArray.parseArray(line);
//          JSONObject jsonObject = jsonArray.getJSONObject(0);
//          //      System.out.println(line);
//          //      Date start = jsonObject.getDate("currentTime");
//          //      long startTime = start.getTime();
//          //      if (startTime > 1684203474000L) {
//          //       FileWriter lineWriter = new FileWriter(potion, true);
//          //       PrintWriter pw = new PrintWriter(lineWriter);
//          //       pw.println(line);
//          //       pw.flush();
//          //       lineWriter.flush();
//          //       pw.close();
//          //       lineWriter.close();
//          //      }
//            Date date = jsonObject.getDate("currentTime");
//            if (date.getTime() > sdf.parse("2023-05-15 15:06:35").getTime()) {
//                       FileWriter lineWriter = new FileWriter(potion, true);
//                       PrintWriter pw = new PrintWriter(lineWriter);
//                       pw.println(line);
//                       pw.flush();
//                       lineWriter.flush();
//                       pw.close();
//                       lineWriter.close();
//            }
////          String longitude = jsonObject.getString("longitude");
////          String latitude = jsonObject.getString("latitude");
////          String key = longitude + ":" + latitude;
////          map.put(key, line);
//        }
//      }
//    }
////
////
////      String filePath = "D:\\dpi-515\\logs\\1684066441471-success\\obs.txt";
////   File potion = new File("D:\\dpi-515\\quchong\\obs529-2.txt");
////    try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
////      for (String line = null; (line = br.readLine()) != null; ) {
////        if (!"[]".equals(line)) {
////          JSONArray jsonArray = JSONArray.parseArray(line);
////          JSONObject jsonObject = jsonArray.getJSONObject(0);
//////          System.out.println(line);
//////          Date start = jsonObject.getDate("currentTime");
//////          long startTime = start.getTime();
//////          if (startTime > 1684203474000L) {
//////            FileWriter lineWriter = new FileWriter(potion, true);
//////            PrintWriter pw = new PrintWriter(lineWriter);
//////            pw.println(line);
//////            pw.flush();
//////            lineWriter.flush();
//////            pw.close();
//////            lineWriter.close();
//////          }
////          String longitude = jsonObject.getString("longitude");
////          String latitude = jsonObject.getString("latitude");
////          String key = longitude + ":" + latitude;
////          map.put(key, line);
////        }
////      }
////    }
////
////
////       String filePath = "D:\\dpi-515\\logs\\1684066441471-success\\speed.txt";
////      File potion = new File("D:\\dpi-515\\quchong\\speed529-2.txt");
////      try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
////          for (String line = null; (line = br.readLine()) != null;) {
////           if (!"{}".equals(line)) {
////            JSONObject jsonObject = JSONObject.parseObject(line);
//////            System.out.println(line);
//////            Date start = jsonObject.getDate("currentTime");
//////            long startTime = start.getTime();
//////            if (startTime > 1684203474000L) {
//////             FileWriter lineWriter = new FileWriter(potion, true);
//////             PrintWriter pw = new PrintWriter(lineWriter);
//////             pw.println(line);
//////             pw.flush();
//////             lineWriter.flush();
//////             pw.close();
//////             lineWriter.close();
//////           }
////               if (jsonObject.getInteger("speed") != 0) {
////                   FileWriter lineWriter = new FileWriter(potion, true);
////                   PrintWriter pw = new PrintWriter(lineWriter);
////                   pw.println(line);
////                   pw.flush();
////                   lineWriter.flush();
////                   pw.close();
////                   lineWriter.close();
////               }
////              }
////          }
//      }
//
//
////
////
////      map.forEach((k, v) -> {
////          try {
////              FileWriter lineWriter = new FileWriter(potion, true);
////              PrintWriter pw = new PrintWriter(lineWriter);
////                  pw.println(v);
////                  pw.flush();
////                  lineWriter.flush();
////                  pw.close();
////              lineWriter.close();
////          } catch (IOException e) {
////              throw new RuntimeException(e);
////          }
////      });
//
//
//
////  }
//}
