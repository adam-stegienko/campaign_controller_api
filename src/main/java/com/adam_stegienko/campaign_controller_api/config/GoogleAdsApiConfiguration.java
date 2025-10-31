package com.adam_stegienko.campaign_controller_api.config;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v21.services.CampaignServiceClient;
import com.google.ads.googleads.v21.services.GoogleAdsServiceClient;
import com.google.auth.oauth2.ServiceAccountCredentials;

@Configuration
public class GoogleAdsApiConfiguration {

    @Value("${api.googleads.developerToken}")
    private String developerToken;

    @Value("${api.googleads.serviceAccountSecretsPath}")
    private String serviceAccountSecretsPath;

    @Value("${api.googleads.loginCustomerId}")
    private String loginCustomerId;

    @Bean
    public GoogleAdsClient googleAdsClient() throws IOException {
        ServiceAccountCredentials credentials = ServiceAccountCredentials
                .fromStream(new FileInputStream(serviceAccountSecretsPath));

        return GoogleAdsClient.newBuilder()
                .setDeveloperToken(developerToken)
                .setCredentials(credentials)
                .setLoginCustomerId(Long.valueOf(loginCustomerId))
                .build();
    }

    @Bean
    public GoogleAdsServiceClient googleAdsServiceClient(GoogleAdsClient googleAdsClient) {
        return googleAdsClient.getVersion21().createGoogleAdsServiceClient();
    }

    @Bean
    public CampaignServiceClient campaignServiceClient(GoogleAdsClient googleAdsClient) {
        return googleAdsClient.getVersion21().createCampaignServiceClient();
    }
}
