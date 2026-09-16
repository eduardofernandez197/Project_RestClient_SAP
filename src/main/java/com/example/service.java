package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class service {

    private final RestClient client;

  public service(RestClient.Builder builder) {
        this.client = builder.build();
    }
        @Value ("${sap.url}")
        private String sapUrl;

        @Value ("${sap.client}")
        private String sapClient;

        @Value ("${sap.user}")
        private String sapUser;

        @Value ("${sap.password}")
        private String sapPassword;

    public String buscaOrdem(String id){
        
    try {

        return client.get()
            .uri(
                sapUrl+"/sap/opu/odata/sap/API_MAINTENANCEORDER;v=2/MaintenanceOrder('"+id+"')?sap-client="+sapClient+"&$format=json"
            )
            .headers(headers -> {
                headers.setBasicAuth(
                     sapUser,
                     sapPassword
                );

                headers.add(
                    "Accept",
                    "application/json"
                );
            })
            .retrieve()
            .body(String.class);

    } catch(Exception e) {

        e.printStackTrace();

        return e.getMessage();
    }
}


}
