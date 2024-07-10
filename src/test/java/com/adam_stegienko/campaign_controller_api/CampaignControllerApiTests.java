package com.adam_stegienko.campaign_controller_api;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.adam_stegienko.campaign_controller_api.controller.PlannerBookController;
import com.adam_stegienko.campaign_controller_api.model.PlannerBook;
import com.adam_stegienko.campaign_controller_api.repositories.PlannerBookRepository;
import com.adam_stegienko.campaign_controller_api.services.PlannerBookService;

@SpringBootTest(classes = {CampaignControllerApi.class, PlannerBookRepository.class, PlannerBookController.class, PlannerBookService.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CampaignControllerApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlannerBookService plannerBookService;

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

    @Test
    void shouldReturnPlannerBookById() throws Exception {
        UUID uuid = UUID.randomUUID();
        mockMvc.perform(get("/v1/api/plannerbooks/" + uuid.toString()))
            .andExpect(status().isOk());
    }

    @Test
    void shouldCreatePlannerBook() throws Exception {
        String newPlannerBookJson = "{"
            + "\"campaign\":\"Test Campaign\","
            + "\"action\":1,"
            + "\"execution_date\":\"2022-01-01 00:00:00\""
            + "}";

        mockMvc.perform(post("/v1/api/plannerbooks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(newPlannerBookJson))
            .andExpect(status().isOk());
    }

    @Test
    void shouldUpdatePlannerBook() throws Exception {
        UUID uuid = UUID.randomUUID();
        String updatedPlannerBookJson = "{"
            + "\"campaign\":\"Updated Campaign\","
            + "\"action\":0,"
            + "\"execution_date\":\"2022-01-02 00:00:00\""
            + "}";

        mockMvc.perform(put("/v1/api/plannerbooks/" + uuid.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatedPlannerBookJson))
            .andExpect(status().isOk());
    }

    @Test
    void shouldDeletePlannerBook() throws Exception {
        UUID uuid = UUID.randomUUID();
        mockMvc.perform(delete("/v1/api/plannerbooks/" + uuid.toString()))
            .andExpect(status().isOk());
    }

    @BeforeEach
    public void setUp() {
        plannerBookService.clearEmitters();
    }

    @Test
    public void testCheckTimestamps() {
        PlannerBook pastBook1 = new PlannerBook();
        pastBook1.setExecutionDate(LocalDateTime.now().minusDays(1));

        PlannerBook pastBook2 = new PlannerBook();
        pastBook2.setExecutionDate(LocalDateTime.now().minusDays(2));

        when(plannerBookRepository.findAll()).thenReturn(Arrays.asList(pastBook1, pastBook2));

        plannerBookService.checkTimestamps();

        verify(plannerBookRepository, atLeastOnce()).findAll();
    }

    @Test
    public void testGetSseEmitter() {
        SseEmitter emitter = plannerBookService.getSseEmitter();
        assertNotNull(emitter);
        assertTrue(plannerBookService.getEmitters().contains(emitter));

        // emitter.complete();
        // assertFalse(plannerBookService.getEmitters().contains(emitter));
    }

}