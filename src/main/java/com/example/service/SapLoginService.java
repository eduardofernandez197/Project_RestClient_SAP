package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service 
public class SapLoginService {
    private final RestClient client;

    public SapLoginService(RestClient.Builder builder,
        @Value ("${sap.url}") String sapUrl){

             this.client = builder
                .baseUrl(sapUrl)
                .build();
    }

    public ResponseEntity<Void> autenticar(String authorization) {
        return client.get()
                .uri("/sap/opu/odata/sap/API_MAINTENANCEORDER;v=2")
                .header("Authorization", authorization)
                .header("X-CSRF-Token", "Fetch")
                .retrieve()
                .toBodilessEntity();
    }
 }


    
