package com.github.ivanfomkin.bookshop.dto.payment;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class PaymentRequestDto {
    private String sum;
    private Instant time;

    public LocalDateTime getLocalDateTime() {
        return this.time.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
