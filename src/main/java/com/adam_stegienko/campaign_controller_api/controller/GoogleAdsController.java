package com.adam_stegienko.campaign_controller_api.controller;

import com.adam_stegienko.campaign_controller_api.services.GoogleAdsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoogleAdsController {

    private final GoogleAdsService googleAdsService;

    @Autowired
    public GoogleAdsController(GoogleAdsService googleAdsService) {
        this.googleAdsService = googleAdsService;
    }

    @GetMapping("/google-ads/search")
    public String searchGoogleAds(@RequestParam String customerId) {
        return googleAdsService.performSearch(customerId);
    }
}
