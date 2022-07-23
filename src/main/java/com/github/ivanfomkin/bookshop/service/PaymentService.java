package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.dto.payment.PaymentRequestDto;
import com.github.ivanfomkin.bookshop.dto.payment.RobokassaPaymentResultDto;

import java.security.NoSuchAlgorithmException;

public interface PaymentService {

    String getPaymentUrl(PaymentRequestDto paymentRequest) throws NoSuchAlgorithmException;

    boolean checkPaymentResult(Double outSum, Long invId, String signatureValue) throws NoSuchAlgorithmException;

    RobokassaPaymentResultDto approvePayment(String outSum, Long invId, String signatureValue, Double fee, String email) throws NoSuchAlgorithmException;

    void makePaymentFailed(Long invId);
}
