package com.tyut.hardwaremall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.tyut.hardwaremall.dao")
@SpringBootApplication
public class HardwareMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(HardwareMallApplication.class, args);
    }

}
