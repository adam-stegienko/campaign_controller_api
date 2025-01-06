package com.adam_stegienko.campaign_controller_api.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.ads.googleads.lib.utils.FieldMasks;
import com.google.ads.googleads.v18.enums.CampaignStatusEnum.CampaignStatus;
import com.google.ads.googleads.v18.resources.Campaign;
import com.google.ads.googleads.v18.services.CampaignOperation;
import com.google.ads.googleads.v18.services.CampaignServiceClient;
import com.google.ads.googleads.v18.services.GoogleAdsRow;
import com.google.ads.googleads.v18.services.GoogleAdsServiceClient;
import com.google.ads.googleads.v18.services.GoogleAdsServiceClient.SearchPagedResponse;
import com.google.ads.googleads.v18.services.MutateCampaignResult;
import com.google.ads.googleads.v18.services.MutateCampaignsResponse;
import com.google.ads.googleads.v18.services.SearchGoogleAdsRequest;
import com.google.ads.googleads.v18.utils.ResourceNames;
import com.google.common.collect.ImmutableList;

@Service
public class GoogleAdsApiService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAdsApiService.class);

    private final GoogleAdsServiceClient googleAdsServiceClient;

    private final CampaignServiceClient campaignServiceClient;

    @Autowired
    public GoogleAdsApiService(GoogleAdsServiceClient googleAdsServiceClient, CampaignServiceClient campaignServiceClient) {
        this.googleAdsServiceClient = googleAdsServiceClient;
        this.campaignServiceClient = campaignServiceClient;
    }

    public String getCampaignStatusByName(String customerId, String campaignName) {
        String query = String.format("SELECT campaign.id, campaign.name, campaign.status FROM campaign WHERE campaign.name = '%s'", campaignName);
        SearchGoogleAdsRequest request = SearchGoogleAdsRequest.newBuilder()
                .setCustomerId(customerId)
                .setQuery(query)
                .build();

        SearchPagedResponse response = googleAdsServiceClient.search(request);
        StringBuilder result = new StringBuilder();

        for (GoogleAdsRow row : response.iterateAll()) {
            result.append(String.format("Campaign ID: %d, Campaign Name: %s, Campaign Status: %s%n",
                    row.getCampaign().getId(),
                    row.getCampaign().getName(),
                    row.getCampaign().getStatus()));
        }

        logger.info("Google Ads API response: {}", result.toString());
        return result.toString();
    }

    public String getCampaignStatusByNamesList(List<String> campaignNames, String customerId) {
        String names = campaignNames.stream()
                .map(name -> String.format("'%s'", name))
                .collect(Collectors.joining(", "));
        String query = String.format("SELECT campaign.id, campaign.name, campaign.status FROM campaign WHERE campaign.name IN (%s)", names);
        SearchGoogleAdsRequest request = SearchGoogleAdsRequest.newBuilder()
                .setCustomerId(customerId)
                .setQuery(query)
                .build();

        SearchPagedResponse response = googleAdsServiceClient.search(request);
        StringBuilder result = new StringBuilder();

        for (GoogleAdsRow row : response.iterateAll()) {
            result.append(String.format("Campaign ID: %d, Campaign Name: %s, Campaign Status: %s%n",
                    row.getCampaign().getId(),
                    row.getCampaign().getName(),
                    row.getCampaign().getStatus()));
        }

        logger.info("Google Ads API response: {}", result.toString());
        return result.toString();
    }

    @SuppressWarnings("UseSpecificCatch")
    public String updateCampaignStatusByName(String customerId, String campaignName, String status) {
        try {
            // Retrieve the campaign ID by name
            String campaignId = getCampaignIdByName(customerId, campaignName);
            if (campaignId == null) {
                String message = String.format("No campaign found with name '%s' for customer ID '%s'", campaignName, customerId);
                logger.error(message);
                return message;
            }

            // Convert status string to CampaignStatus enum
            CampaignStatus campaignStatus = CampaignStatus.valueOf(status.toUpperCase());

            // Create the campaign with the updated status
            Campaign campaign = Campaign.newBuilder()
                    .setResourceName(ResourceNames.campaign(Long.parseLong(customerId), Long.parseLong(campaignId)))
                    .setStatus(campaignStatus)
                    .build();

            // Create the operation
            CampaignOperation operation = CampaignOperation.newBuilder()
                    .setUpdate(campaign)
                    .setUpdateMask(FieldMasks.allSetFieldsOf(campaign))
                    .build();

            // Update the campaign
            MutateCampaignsResponse response = campaignServiceClient.mutateCampaigns(customerId, ImmutableList.of(operation));
            MutateCampaignResult result = response.getResults(0);

            logger.info(result.toString());
            logger.info("Google Ads API response: Campaign status updated successfully for campaign '{}'", campaignName);
            return String.format("Campaign status updated successfully for campaign '%s'", campaignName);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to update campaign status for campaign '%s' with customer ID '%s': %s", campaignName, customerId, e.getMessage());
            logger.error(errorMessage, e);
            return errorMessage;
        }
    }

    private String getCampaignIdByName(String customerId, String campaignName) {
        String query = String.format("SELECT campaign.id FROM campaign WHERE campaign.name = '%s'", campaignName);
        SearchGoogleAdsRequest request = SearchGoogleAdsRequest.newBuilder()
                .setCustomerId(customerId)
                .setQuery(query)
                .build();

        SearchPagedResponse response = googleAdsServiceClient.search(request);
        for (GoogleAdsRow row : response.iterateAll()) {
            return String.valueOf(row.getCampaign().getId());
        }
        return null;
    }
}
