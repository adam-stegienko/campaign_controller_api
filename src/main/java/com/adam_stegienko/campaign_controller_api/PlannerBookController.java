package com.adam_stegienko.campaign_controller_api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class PlannerBookController {
    
    @GetMapping("/v1/api/plannerbooks")
    public List<String> getPlannerBooks() throws IOException {
        return Files.readAllLines(Paths.get("plannerbooks.json"));
    }
}
