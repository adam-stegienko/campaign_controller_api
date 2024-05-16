package com.adam_stegienko.campaign_controller_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.adam_stegienko.campaign_controller_api.controller.PlannerBookController;
import com.adam_stegienko.campaign_controller_api.repository.PlannerBookRepository;

@SpringBootTest(classes = {CampaignControllerApi.class, PlannerBookRepository.class, PlannerBookController.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CampaignControllerApiTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlannerBookRepository plannerBookRepository;

    @Test
    void shouldReturnCampaignControllerApp() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturnPlannerBooks() throws Exception {
        mockMvc.perform(get("/v1/api/plannerbooks"))
            .andExpect(status().isOk());
    }

    public PlannerBookRepository getPlannerBookRepository() {
        return plannerBookRepository;
    }

    public void setPlannerBookRepository(PlannerBookRepository plannerBookRepository) {
        this.plannerBookRepository = plannerBookRepository;
    }

}