package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.dto.payment.PaymentRequestDto;
import com.github.ivanfomkin.bookshop.dto.payment.RobokassaPaymentResultDto;
import com.github.ivanfomkin.bookshop.entity.enums.TransactionType;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.service.PaymentService;
import com.github.ivanfomkin.bookshop.service.TransactionService;
import com.github.ivanfomkin.bookshop.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.StringJoiner;

@Slf4j
@Service
public class PaymentServiceRobokassaImp implements PaymentService {
    private static final String ALGORITHM = "MD5";
    private final TransactionService transactionService;
    private final UserService userService;

    @Value("${bookshop.payments.robokassa.merchant.login}")
    private String merchantLogin;

    @Value("${bookshop.payments.robokassa.passwords.first}")
    private String firstPassword;

    @Value("${bookshop.payments.robokassa.passwords.second}")
    private String secondPassword;

    public PaymentServiceRobokassaImp(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @Override
    public String getPaymentUrl(PaymentRequestDto paymentRequest) throws NoSuchAlgorithmException {
        var paymentSum = Double.parseDouble(paymentRequest.getSum());
        var transaction = transactionService.createTransaction(paymentSum, TransactionType.DEPOSIT);
        var invId = transaction.getId();
        return "https://auth.robokassa.ru/Merchant/Index.aspx" +
                "?MerchantLogin=" + merchantLogin +
                "&InvId=" + invId +
                "&Culture=ru" +
                "&Encoding=utf-8" +
                "&OutSum=" + formatDouble(paymentSum) +
                "&SignatureValue=" + buildHash(paymentSum, invId.toString()) +
                "&IsTest=1";
    }

    private String buildHash(Double sum, String invId) throws NoSuchAlgorithmException {
        var stringFotHash = new StringJoiner(":").add(merchantLogin).add(formatDouble(sum)).add(invId).add(firstPassword).toString();
        return hashString(stringFotHash);
    }

    private String buildHash(Double outSum, String invId, String password) throws NoSuchAlgorithmException {
        return this.buildHash(formatDouble(outSum), invId, password);
    }

    private String buildHash(String outSum, String invId, String password) throws NoSuchAlgorithmException {
        var stringForHash = new StringJoiner(":").add(outSum).add(invId).add(password).toString();
        return hashString(stringForHash);
    }

    @Override
    public boolean checkPaymentResult(Double outSum, Long invId, String signatureValue) throws NoSuchAlgorithmException {
        String calculatedHash = this.buildHash(outSum, invId.toString(), firstPassword);
        return signatureValue.equalsIgnoreCase(calculatedHash);
    }

    @Override
    @Transactional
    public RobokassaPaymentResultDto approvePayment(String outSum, Long invId, String signatureValue, Double fee, String email) throws NoSuchAlgorithmException {
        var transaction = transactionService.findTransactionById(invId);
        var calculatedSignatureValue = buildHash(outSum, invId.toString(), secondPassword);
        var paymentResult = calculatedSignatureValue.equalsIgnoreCase(signatureValue) && transaction.getAmount().equals(Double.parseDouble(outSum));
        transactionService.setTransactionResult(transaction, paymentResult);
        if (paymentResult) {
            userService.updateUserBalance(transaction.getUser(), transaction.getAmount(), TransactionType.DEPOSIT);
        }
        var resultDto = RobokassaPaymentResultDto.of(paymentResult, invId);
        log.info(resultDto.toString());
        return resultDto;
    }

    @Override
    @Transactional
    public void makePaymentFailed(Long invId) {
        var transaction = transactionService.findTransactionById(invId);
        transactionService.setTransactionResult(transaction, false);
    }

    private String hashString(String string) throws NoSuchAlgorithmException {
        var messageDigest = MessageDigest.getInstance(ALGORITHM);
        messageDigest.update(string.getBytes());
        return DatatypeConverter.printHexBinary(messageDigest.digest()).toUpperCase();
    }

    private String formatDouble(Double doubleValue) {
        return String.format(Locale.ENGLISH, "%.2f", doubleValue);
    }
}
