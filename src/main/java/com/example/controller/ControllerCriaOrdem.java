package com.example.controller;

import com.example.dto.request.CriarOrdemRequest;
import com.example.service.ServiceCriaOrdem;

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
public class ControllerCriaOrdem {

    private final ServiceCriaOrdem service;

    public ControllerCriaOrdem(ServiceCriaOrdem service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> criar(
            @RequestBody CriarOrdemRequest ordem,
            HttpServletRequest request) {

        HttpSession sessao = request.getSession(false);

        if (sessao == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Faça login antes de criar uma ordem.");
        }

        String authorization =
                (String) sessao.getAttribute("SAP_AUTHORIZATION");

        String cookies =
                (String) sessao.getAttribute("SAP_COOKIES");

        String csrfToken =
                (String) sessao.getAttribute("SAP_CSRF_TOKEN");

        if (authorization == null || cookies == null || csrfToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Sessão incompleta. Faça login novamente.");
        }

        try {
            ResponseEntity<String> resposta = service.criarOrdem(
                    ordem, authorization, cookies, csrfToken);

            MediaType tipo = resposta.getHeaders().getContentType();

            return ResponseEntity.status(resposta.getStatusCode())
                    .contentType(
                            tipo != null ? tipo : MediaType.APPLICATION_JSON)
                    .body(resposta.getBody());

        } catch (HttpClientErrorException e) {
            int status = e.getStatusCode().value();

            if (status == 401) {
                sessao.invalidate();

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("O SAP recusou a autenticação. Faça login novamente.");
            }

            if (status == 403) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("O SAP recusou a operação. Verifique as permissões "
                                + "ou a validade do token CSRF e da sessão.");
            }

            if (status == 400) {
                return ResponseEntity.badRequest()
                         .contentType(MediaType.TEXT_PLAIN)
            .body(e.getResponseBodyAsString());
            }

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("O SAP rejeitou a criação da ordem.");

        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Não foi possível confirmar a criação. "
                            + "Consulte o SAP antes de repetir a operação.");
        }
    }
}