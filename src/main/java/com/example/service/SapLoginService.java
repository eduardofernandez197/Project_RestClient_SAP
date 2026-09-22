package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SapLoginService {

    private final RestClient client;
    private final String sapClient;

    public SapLoginService(
            RestClient.Builder builder,
            @Value("${sap.url}") String sapUrl,
            @Value("${sap.client}") String sapClient) {

        this.client = builder.baseUrl(sapUrl).build();
        this.sapClient = sapClient;
    }

    public ResponseEntity<Void> autenticar(String authorization) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/sap/opu/odata/sap/API_MAINTENANCEORDER;v=2")
                        .queryParam("sap-client", sapClient)
                        .build())
                .header("Authorization", authorization)
                .header("X-CSRF-Token", "Fetch")
                .retrieve()
                .toBodilessEntity();
    }
}