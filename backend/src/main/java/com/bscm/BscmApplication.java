package com.bscm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BscmApplication {
  public static void main(String[] args) {
    SpringApplication.run(BscmApplication.class, args);
  }
}
