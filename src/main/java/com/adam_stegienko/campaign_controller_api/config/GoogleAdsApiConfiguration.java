package com.adam_stegienko.campaign_controller_api.config;

import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v18.services.GoogleAdsServiceClient;
import com.google.auth.oauth2.UserCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    // @Value("${api.googleads.customerId}")
    // private String customerId;

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

    // @SuppressWarnings("CallToPrintStackTrace")
    // public void searchCampaigns() {
    //     try (GoogleAdsServiceClient googleAdsServiceClient = googleAdsClient().getLatestVersion().createGoogleAdsServiceClient()) {
    //         googleAdsServiceClient.search(customerId, "SELECT campaign.id FROM campaign");
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }
}
