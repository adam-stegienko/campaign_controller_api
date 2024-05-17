package com.adam_stegienko.campaign_controller_api.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adam_stegienko.campaign_controller_api.model.PlannerBook;

@Repository
public interface PlannerBookRepository extends JpaRepository<PlannerBook, UUID> {

}