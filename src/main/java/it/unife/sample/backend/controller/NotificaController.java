package it.unife.sample.backend.controller;

import it.unife.sample.backend.dto.response.NotificaResponse;
import it.unife.sample.backend.service.NotificaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifiche")
@RequiredArgsConstructor
public class NotificaController {

    private final NotificaService notificaService;

    @GetMapping
    public List<NotificaResponse> getMie(@RequestHeader("X-User-Id") Long idUtente) {
        return notificaService.getMie(idUtente);
    }

    @GetMapping("/badge")
    public long getBadge(@RequestHeader("X-User-Id") Long idUtente) {
        return notificaService.countNonLette(idUtente);
    }

    @PutMapping("/{id}/letta")
    public ResponseEntity<Void> segnaLetta(@PathVariable Long id) {
        notificaService.segnaLetta(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/leggi-tutte")
    public ResponseEntity<Void> segnaLutteLette(@RequestHeader("X-User-Id") Long idUtente) {
        notificaService.segnaLutteLette(idUtente);
        return ResponseEntity.ok().build();
    }
}
