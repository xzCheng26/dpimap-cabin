package com.dpi.map.cabin;

import cn.hutool.core.collection.CollectionUtil;
import com.dpi.map.cabin.servlet.BeanContext;
import com.github.benmanes.caffeine.cache.Cache;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@MapperScan(basePackages = "com.dpi.map.cabin.mapper")
public class CabinApplication {
  public static void main(String[] args) {
    SpringApplication.run(CabinApplication.class,args);
    if (args.length != 0) {
      Cache<String, Object> cache = BeanContext.getBean(Cache.class);
      boolean cacheFlag = Boolean.parseBoolean(args[0]);
      String basePath = args[1];
      cache.put("cacheFlag", cacheFlag);
      cache.put("basePath", basePath);
    }

  }
}
