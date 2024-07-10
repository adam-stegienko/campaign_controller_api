package com.adam_stegienko.campaign_controller_api.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
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

    @Autowired
    public PlannerBookService(PlannerBookRepository plannerBookRepository) {
        this.plannerBookRepository = plannerBookRepository;
    }

    public SseEmitter getSseEmitter() {
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
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
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("plannerBookEvent").data(plannerBook));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}