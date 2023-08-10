package com.hmdp;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@MapperScan("com.hmdp.mapper")
@SpringBootApplication
@Slf4j
@EnableAspectJAutoProxy(exposeProxy = true)
public class HmDianPingApplication {

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        SpringApplication.run(HmDianPingApplication.class, args);
        stopwatch.stop();
        log.info("项目启动耗时为{}",stopwatch.elapsed().getSeconds());
    }
}
