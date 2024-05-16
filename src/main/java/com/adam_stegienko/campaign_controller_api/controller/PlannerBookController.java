package com.adam_stegienko.campaign_controller_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adam_stegienko.campaign_controller_api.model.PlannerBook;
import com.adam_stegienko.campaign_controller_api.repository.PlannerBookRepository;

@RestController
@RequestMapping("v1/api/plannerbooks")
public class PlannerBookController {

    private final PlannerBookRepository plannerBookRepository;

    @Autowired
    public PlannerBookController(PlannerBookRepository plannerBookRepository) {
        this.plannerBookRepository = plannerBookRepository;
    }

    @GetMapping
    public List<PlannerBook> getAllPlannerBooks() {
        return plannerBookRepository.findAll();
    }
}