package com.github.ivanfomkin.bookshop.dto.payment;

public record RobokassaPaymentResultDto(String result) {
    public static RobokassaPaymentResultDto of(boolean result, long invId) {
        var stringResult = result ? "OK" + invId : "bad sign";
        return new RobokassaPaymentResultDto(stringResult);
    }
}
