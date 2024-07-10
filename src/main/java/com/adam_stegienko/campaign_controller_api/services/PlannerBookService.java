package com.adam_stegienko.campaign_controller_api.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.adam_stegienko.campaign_controller_api.model.PlannerBook;
import com.adam_stegienko.campaign_controller_api.repositories.PlannerBookRepository;

@Service
public class PlannerBookService {

    private final PlannerBookRepository plannerBookRepository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final Set<String> sentEventIds = new HashSet<>();

    @Autowired
    public PlannerBookService(PlannerBookRepository plannerBookRepository) {
        this.plannerBookRepository = plannerBookRepository;
    }

    public SseEmitter getSseEmitter() {
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(emitter);
        });
        return emitter;
    }

    public List<SseEmitter> getEmitters() {
        return emitters;
    }

    public void clearEmitters() {
        emitters.clear();
    }

    @Scheduled(fixedRate = 5000) // check every 6 seconds
    public void checkTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        List<PlannerBook> expiredBooks = plannerBookRepository.findAll().stream()
            .filter(book -> book.getExecutionDate() != null && book.getExecutionDate().isBefore(now))
            .collect(Collectors.toList());

        expiredBooks.forEach(this::sendEvent);
    }

    private void sendEvent(PlannerBook plannerBook) {
        String eventId = String.valueOf(plannerBook.getId());
        if (sentEventIds.contains(eventId)) {
            return; //
        }

        List<SseEmitter> failedEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().id(eventId).name("plannerBookEvent").data(plannerBook));
                sentEventIds.add(eventId); // Add event ID to the set after successful send
            } catch (IOException e) {
                failedEmitters.add(emitter);
            }
        }
        emitters.removeAll(failedEmitters);
    }
}