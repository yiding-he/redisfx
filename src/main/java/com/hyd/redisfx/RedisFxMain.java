package com.hyd.redisfx;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisFxMain {

  public static void main(String[] args) {
    SpringApplication.run(RedisFxMain.class, args);
    Application.launch(RedisFxApp.class, args);
  }
}
