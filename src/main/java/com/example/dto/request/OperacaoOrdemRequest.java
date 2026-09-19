package com.example.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;

public record OperacaoOrdemRequest(

        @JsonProperty("MaintenanceOrderOperation")
        String numeroOperacao,

        @JsonProperty("OperationDescription")
        String descricao,

        @JsonProperty("OperationControlKey")
        String chaveControle,

        @JsonProperty("WorkCenter")
        String centroTrabalho,

        @JsonProperty("Plant")
        String centro,

        @JsonProperty("ActivityType")
        String tipoAtividade,

        @JsonProperty("MaintOrdOperationWorkDuration")
        String trabalho,

        @JsonProperty("MaintOrdOpWorkDurationUnit")
        String unidadeTrabalho,

        @JsonProperty("MaintOrderOperationDuration")
        String duracao,

        @JsonProperty("MaintOrdOperationDurationUnit")
        String unidadeDuracao,

        @JsonProperty("NumberOfCapacities")
        Integer numeroCapacidades

) {
}