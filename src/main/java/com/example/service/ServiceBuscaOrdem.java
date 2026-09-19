package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ServiceBuscaOrdem {

    private final RestClient client;
    private final String sapClient;

    public ServiceBuscaOrdem(
            RestClient.Builder builder,
            @Value("${sap.url}") String sapUrl,
            @Value("${sap.client}") String sapClient) {

        this.client = builder.baseUrl(sapUrl).build();
        this.sapClient = sapClient;
    }

    public String buscaOrdem(
            String id,
            String authorization,
            String cookies) {

        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/sap/opu/odata/sap/API_MAINTENANCEORDER;v=2"
                                + "/MaintenanceOrder('{id}')")
                        .queryParam("sap-client", sapClient)
                        .queryParam("$format", "json")
                        .build(id))
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .header(HttpHeaders.COOKIE, cookies)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);
    }
}