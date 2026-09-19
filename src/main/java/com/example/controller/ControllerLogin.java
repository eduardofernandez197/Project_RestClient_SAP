package com.example.controller;

import org.springframework.http.HttpHeaders;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;


import com.example.service.SapLoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
public class ControllerLogin {

    private final SapLoginService service;

    public ControllerLogin(SapLoginService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestHeader("Authorization") String authorization,
            HttpServletRequest request) {

        if (!authorization.regionMatches(true, 0, "Basic ", 0, 6)
                || authorization.substring(6).isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Envie Authorization no formato Basic <credenciais>.");
        }

        try {
            ResponseEntity<Void> resposta = service.autenticar(authorization);

            // Obtém o token e os cookies retornados pelo SAP.
            String csrfToken = resposta.getHeaders()
                    .getFirst("X-CSRF-Token");

            List<String> cookiesRecebidos = resposta.getHeaders()
                    .get(HttpHeaders.SET_COOKIE);

            // Só cria a sessão se a resposta estiver completa.
            if (!resposta.getStatusCode().is2xxSuccessful()
                    || csrfToken == null
                    || csrfToken.isBlank()
                    || cookiesRecebidos == null
                    || cookiesRecebidos.isEmpty()) {

                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("O SAP não retornou os dados necessários para a sessão.");
            }

            // Mantém apenas nome=valor de cada cookie.
            String cookiesSap = cookiesRecebidos.stream()
                    .map(cookie -> cookie.split(";", 2)[0])
                    .collect(Collectors.joining("; "));

            // Descarta a sessão anterior após autenticar com sucesso.
            HttpSession sessaoAnterior = request.getSession(false);

            if (sessaoAnterior != null) {
                sessaoAnterior.invalidate();
            }

            // Cria uma sessão e armazena os dados SAP no servidor.
            HttpSession sessao = request.getSession(true);

            sessao.setAttribute("SAP_AUTHORIZATION", authorization);
            sessao.setAttribute("SAP_CSRF_TOKEN", csrfToken);
            sessao.setAttribute("SAP_COOKIES", cookiesSap);

            // Expira após 15 minutos sem utilização.
            sessao.setMaxInactiveInterval(15 * 60);

            return ResponseEntity.ok("Login realizado.");
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 401) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("O SAP recusou as credenciais.");
            }

            if (e.getStatusCode().value() == 403) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("O SAP negou acesso ao recurso.");
            }

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("O SAP rejeitou a chamada de autenticação.");

        } catch (RestClientException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Não foi possível concluir a chamada ao SAP.");
        }

    }

}
