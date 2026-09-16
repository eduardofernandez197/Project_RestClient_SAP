package com.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ordens")
public class controller {

    private final service service;

    public controller(service service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable("id") String id) {

        return ResponseEntity.ok(
            service.buscaOrdem(id)
        );
    }
}