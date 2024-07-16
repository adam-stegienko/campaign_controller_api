package com.adam_stegienko.campaign_controller_api.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.adam_stegienko.campaign_controller_api.services.PlannerBookService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Controller
public class EventController {

    private final PlannerBookService plannerBookService;
    private static final Logger LOGGER = LoggerFactory.getLogger(PlannerBookController.class);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Autowired
    public EventController(PlannerBookService plannerBookService) {
        this.plannerBookService = plannerBookService;
    }

    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.error(e.toString());
            }
        }));
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin
    public SseEmitter streamPlannerBookEvents() {
        final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitter.onCompletion(() -> LOGGER.info("SseEmitter is completed"));
        emitter.onTimeout(() -> LOGGER.info("SseEmitter is timed out"));
        emitter.onError((ex) -> LOGGER.info("SseEmitter got error:", ex));
        
        ObjectMapper objectMapper = new ObjectMapper();

        executor.execute(() -> {
            while (true) { // Or some condition to stop the loop
                String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy-HH:mm:ss"));
                Map<String, String> dateTimeMap = new HashMap<>();
                dateTimeMap.put("dateTime", dateTime);
                String json = objectMapper.writeValueAsString(dateTimeMap);

                emitter.send(SseEmitter.event().data(json));
                sleep(1, emitter);
            }
        });

        return emitter;

    }
    // public SseEmitter streamDateTime() {

    //     SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

    //     sseEmitter.onCompletion(() -> LOGGER.info("SseEmitter is completed"));

    //     sseEmitter.onTimeout(() -> LOGGER.info("SseEmitter is timed out"));

    //     sseEmitter.onError((ex) -> LOGGER.info("SseEmitter got error:", ex));

    //     // Inside your method
    //     ObjectMapper objectMapper = new ObjectMapper();

    //     executor.execute(() -> {
    //         for (int i = 0; i < 15; i++) {
    //             try {
    //                 String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy-HH:mm:ss"));
    //                 Map<String, String> dateTimeMap = new HashMap<>();
    //                 dateTimeMap.put("dateTime", dateTime);
    //                 String json = objectMapper.writeValueAsString(dateTimeMap);
                    
    //                 sseEmitter.send(json);
    //                 sleep(1, sseEmitter);
    //             } catch (IOException e) {
    //                 sseEmitter.completeWithError(e);
    //             }
    //         }
    //         sseEmitter.complete();
    //     });

    //     LOGGER.info("Controller exits");
    //     return sseEmitter;
    // }

    private void sleep(int seconds, SseEmitter sseEmitter) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            sseEmitter.completeWithError(e);
        }
    }
}
