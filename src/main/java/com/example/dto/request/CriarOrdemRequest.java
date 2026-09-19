package com.example.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CriarOrdemRequest(

        @JsonProperty("MaintenanceOrderType")
        String tipoOrdem,

        @JsonProperty("MaintenanceOrderDesc")
        String descricao,

        @JsonProperty("MaintenancePlanningPlant")
        String centroPlanejamento,

        @JsonProperty("Equipment")
        String equipamento,

        @JsonProperty("MainWorkCenter")
        String centroTrabalho,

        @JsonProperty("MainWorkCenterPlant")
        String centro,

        @JsonProperty("MaintPriority")
        String prioridade,

        @JsonProperty("OperationSystemCondition")
        String condicaoSistema,

        @JsonProperty("MaintenanceActivityType")
        String tipoAtividade,

        @JsonProperty("MaintenancePlannerGroup")
        String grupoPlanejamento,

        @JsonProperty("to_MaintenanceOrderOperation")
        List<OperacaoOrdemRequest> operacoes

) {
}