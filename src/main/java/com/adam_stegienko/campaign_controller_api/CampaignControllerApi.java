package com.adam_stegienko.campaign_controller_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class CampaignControllerApi {

  public static void main(String[] args) {
    SpringApplication.run(CampaignControllerApi.class, args);
  }

  @RestController
  class HelloControler {

    @RequestMapping("/")
    public String hello() {
      return "Hello, World!";
    }
  }
}