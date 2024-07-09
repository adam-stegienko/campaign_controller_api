package com.adam_stegienko.campaign_controller_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.adam_stegienko.campaign_controller_api"})
@EntityScan("com.adam_stegienko.campaign_controller_api.model")
@EnableJpaRepositories("com.adam_stegienko.campaign_controller_api.repositories")
@ComponentScan(basePackages = {"com.adam_stegienko.campaign_controller_api.controller"})
public class CampaignControllerApi {

  public static void main(String[] args) {
    SpringApplication.run(CampaignControllerApi.class, args);
  }

}