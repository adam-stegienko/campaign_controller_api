package com.adam_stegienko.campaign_controller_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

  @RequestMapping("/")
  public String campaignControllerMessage() {
    return "Campaign Controller App";
  }
}