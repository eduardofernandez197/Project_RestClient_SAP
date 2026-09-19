package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.dto.request.CriarOrdemRequest;

@Service
public class ServiceCriaOrdem {

    private final RestClient client;
    private final String sapClient;

    public ServiceCriaOrdem(
            RestClient.Builder builder,
            @Value("${sap.url}") String sapUrl,
            @Value("${sap.client}") String sapClient) {

        this.client = builder.baseUrl(sapUrl).build();
        this.sapClient = sapClient;
    }

    public ResponseEntity<String> criarOrdem(
            CriarOrdemRequest ordem,
            String authorization,
            String cookies,
            String csrfToken) {

        return client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/sap/opu/odata/sap/API_MAINTENANCEORDER;v=2"
                                + "/MaintenanceOrder")
                        .queryParam("sap-client", sapClient)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .header(HttpHeaders.COOKIE, cookies)
                .header("X-CSRF-Token", csrfToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(ordem)
                .retrieve()
                .toEntity(String.class);
    }
}