package com.adam_stegienko.campaign_controller_api.services;

import com.google.ads.googleads.v18.services.GoogleAdsServiceClient;
import com.google.ads.googleads.v18.services.GoogleAdsServiceClient.SearchPagedResponse;
import com.google.ads.googleads.v18.services.SearchGoogleAdsRequest;
import com.google.ads.googleads.v18.services.SearchGoogleAdsResponse;
import com.google.ads.googleads.v18.services.GoogleAdsRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GoogleAdsService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAdsService.class);

    private final GoogleAdsServiceClient googleAdsServiceClient;

    @Autowired
    public GoogleAdsService(GoogleAdsServiceClient googleAdsServiceClient) {
        this.googleAdsServiceClient = googleAdsServiceClient;
    }

    public String performSearch(String customerId) {
        String query = "SELECT campaign.id, campaign.name FROM campaign";
        SearchGoogleAdsRequest request = SearchGoogleAdsRequest.newBuilder()
                .setCustomerId(customerId)
                .setQuery(query)
                .build();

        SearchPagedResponse response = googleAdsServiceClient.search(request);
        StringBuilder result = new StringBuilder();

        for (GoogleAdsRow row : response.iterateAll()) {
            result.append(String.format("Campaign ID: %d, Campaign Name: %s%n",
                    row.getCampaign().getId(),
                    row.getCampaign().getName()));
        }

        logger.info("Google Ads API response: {}", result.toString());
        return result.toString();
    }
}
