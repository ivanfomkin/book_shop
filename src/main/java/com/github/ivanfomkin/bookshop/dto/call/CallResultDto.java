package com.github.ivanfomkin.bookshop.dto.call;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CallResultDto {
    private CallStatus status;
    private String code;
    @JsonProperty("call_id")
    private String callId;
    private Double cost;
    private Double balance;
    @JsonProperty("status_text")
    private String statusText;
}
