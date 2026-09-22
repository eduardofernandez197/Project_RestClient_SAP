package com.example.service;

import com.example.dto.request.AtualizarOrdemRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ServiceModificaOrdem {

    private final RestClient client;
    private final String sapClient;

    public ServiceModificaOrdem(
            RestClient.Builder builder,
            @Value("${sap.url}") String sapUrl,
            @Value("${sap.client}") String sapClient) {

        this.client = builder.baseUrl(sapUrl).build();
        this.sapClient = sapClient;
    }

    public ResponseEntity<Void> atualizarOrdem(
            String id,
            AtualizarOrdemRequest ordem,
            String authorization,
            String cookies,
            String csrfToken,
            String etag) {

        return client.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/sap/opu/odata/sap/API_MAINTENANCEORDER;v=2"
                                + "/MaintenanceOrder('{id}')")
                        .queryParam("sap-client", sapClient)
                        .build(id))
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .header(HttpHeaders.COOKIE, cookies)
                .header("X-CSRF-Token", csrfToken)
                .header(HttpHeaders.IF_MATCH, etag)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(ordem)
                .retrieve()
                .toBodilessEntity();
    }
}