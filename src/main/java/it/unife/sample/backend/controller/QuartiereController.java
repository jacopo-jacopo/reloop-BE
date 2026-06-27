package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.response.QuartiereResponse;
import it.unife.sample.backend.service.QuartiereService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quartieri")
@RequiredArgsConstructor
public class QuartiereController {

    private final QuartiereService quartiereService;

    @GetMapping
    public List<QuartiereResponse> getAll() {
        return quartiereService.findAll();
    }
}
