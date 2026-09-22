package com.example.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AtualizarOrdemRequest(

        @JsonProperty("MaintenanceOrderDesc")
        String descricao,

        @JsonProperty("MaintPriority")
        String prioridade

) {
}