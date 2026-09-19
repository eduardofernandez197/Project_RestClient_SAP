package com.example.controller;

import com.example.service.ServiceBuscaOrdem;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@RestController
@RequestMapping("/api/ordens")
public class ControllerBuscaOrdem {

    private final ServiceBuscaOrdem service;

    public ControllerBuscaOrdem(ServiceBuscaOrdem service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> buscar(
            @PathVariable("id") String id,
            HttpServletRequest request) {

        HttpSession sessao = request.getSession(false);

        if (sessao == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Faça login antes de consultar.");
        }

        String authorization =
                (String) sessao.getAttribute("SAP_AUTHORIZATION");

        String cookies =
                (String) sessao.getAttribute("SAP_COOKIES");

        if (authorization == null || cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Sessão inválida. Faça login novamente.");
        }

        try {
            String ordem = service.buscaOrdem(id, authorization, cookies);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ordem);

        } catch (HttpClientErrorException e) {
            int status = e.getStatusCode().value();

            if (status == 401) {
                sessao.invalidate();

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("O SAP recusou a autenticação. Faça login novamente.");
            }

            if (status == 403) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Sem permissão para consultar esta ordem.");
            }

            if (status == 404) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Ordem não encontrada.");
            }

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("O SAP rejeitou a consulta.");

        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Não foi possível concluir a consulta ao SAP.");
        }
    }
}