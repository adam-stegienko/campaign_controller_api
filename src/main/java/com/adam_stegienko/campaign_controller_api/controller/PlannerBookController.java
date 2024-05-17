package com.adam_stegienko.campaign_controller_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adam_stegienko.campaign_controller_api.model.PlannerBook;
import com.adam_stegienko.campaign_controller_api.repositories.PlannerBookRepository;

@RestController
@RequestMapping("v1/api/plannerbooks")
public class PlannerBookController {

    private final PlannerBookRepository plannerBookRepository;

    @Autowired
    public PlannerBookController(PlannerBookRepository plannerBookRepository) {
        this.plannerBookRepository = plannerBookRepository;
    }

    // GET /v1/api/plannerbooks - all planner books
    @GetMapping
    public List<PlannerBook> getAllPlannerBooks() {
        return plannerBookRepository.findAll();
    }

    // GET /v1/api/plannerbooks/{id} - planner book by id
    // @GetMapping(path = "/{id}")
    // public Optional<PlannerBook> getPlannerBooksById(@PathVariable UUID id) {
    //     return plannerBookRepository.findById(id);
    // }

    @GetMapping("/{id}")
    public PlannerBook getPlannerBook(@PathVariable("id") UUID id) {
        return plannerBookRepository.findById(id).orElse(null);
    }

    // POST /v1/api/plannerbooks - create new planner book
    @PostMapping
    public PlannerBook createPlannerBook(@RequestBody PlannerBook newPlannerBook) {
        return plannerBookRepository.save(newPlannerBook);
    }

    // PUT /v1/api/plannerbooks/{id} - update planner book by id
    @PutMapping(path = "/{id}")
    public PlannerBook updatePlannerBook(@PathVariable UUID id, @RequestBody PlannerBook updatedPlannerBook) {
        return plannerBookRepository.findById(id)
            .map(plannerBook -> {
                plannerBook.setCampaign(updatedPlannerBook.getCampaign());
                plannerBook.setAction(updatedPlannerBook.getAction());
                plannerBook.setExecutionDate(updatedPlannerBook.getExecutionDate());
                return plannerBookRepository.save(plannerBook);
            })
            .orElseGet(() -> {
                updatedPlannerBook.setId(id);
                return plannerBookRepository.save(updatedPlannerBook);
            });
    }

    // DELETE /v1/api/plannerbooks/{id} - delete planner book by id
    // @DeleteMapping(path = "/{id}")
    // public void deletePlannerBook(@PathVariable UUID id) {
    //     plannerBookRepository.deleteById(id);
    // }

    @DeleteMapping("/{id}")
    public void deletePlannerBook(@PathVariable("id") UUID id) {
        plannerBookRepository.deleteById(id);
    }

}
