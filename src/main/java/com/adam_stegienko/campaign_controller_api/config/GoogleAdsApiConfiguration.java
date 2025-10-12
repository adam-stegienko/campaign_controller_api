package com.adam_stegienko.campaign_controller_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v21.services.CampaignServiceClient;
import com.google.ads.googleads.v21.services.GoogleAdsServiceClient;
import com.google.auth.oauth2.UserCredentials;

@Configuration
public class GoogleAdsApiConfiguration {

    @Value("${api.googleads.developerToken}")
    private String developerToken;

    @Value("${api.googleads.clientId}")
    private String clientId;

    @Value("${api.googleads.clientSecret}")
    private String clientSecret;

    @Value("${api.googleads.refreshToken}")
    private String refreshToken;

    @Value("${api.googleads.loginCustomerId}")
    private String loginCustomerId;

    @Bean
    public GoogleAdsClient googleAdsClient() {
        UserCredentials credentials = UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(refreshToken)
                .build();

        return GoogleAdsClient.newBuilder()
                .setDeveloperToken(developerToken)
                .setCredentials(credentials)
                .setLoginCustomerId(Long.valueOf(loginCustomerId))
                .build();
    }

    @Bean
    public GoogleAdsServiceClient googleAdsServiceClient(GoogleAdsClient googleAdsClient) {
        return googleAdsClient.getLatestVersion().createGoogleAdsServiceClient();
    }

    @Bean
    public CampaignServiceClient campaignServiceClient(GoogleAdsClient googleAdsClient) {
        return googleAdsClient.getLatestVersion().createCampaignServiceClient();
    }
}
