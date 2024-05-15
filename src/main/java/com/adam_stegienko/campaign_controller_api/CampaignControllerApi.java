package com.adam_stegienko.campaign_controller_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class CampaignControllerApi {

  @Value("${spring.datasource.url}")
  private String datasourceUrl;

  @Value("${spring.datasource.username}")
  private String datasourceUsername;

  @Value("${spring.datasource.password}")
  private String datasourcePassword;

  public static void main(String[] args) {
    SpringApplication.run(CampaignControllerApi.class, args);
  }

  @PostConstruct
  public void migrateFlyway() {
      Flyway flyway = Flyway.configure()
          .dataSource(datasourceUrl, datasourceUsername, datasourcePassword)
          .load();
      flyway.migrate();
  }

  @RestController
  class HelloControler {

    @RequestMapping("/")
    public String campaignControllerMessage() {
      return "Campaign Controller App";
    }
  }
}